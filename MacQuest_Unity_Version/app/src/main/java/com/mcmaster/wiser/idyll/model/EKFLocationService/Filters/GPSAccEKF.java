package com.mcmaster.wiser.idyll.model.EKFLocationService.Filters;

/**
 * Reference: https://github.com/maddevsio/mad-location-manager
 */

import android.util.Log;

import com.mcmaster.wiser.idyll.model.EKFLocationService.Commons.Matrix;

public class GPSAccEKF {
    public static final String TAG = "GPSAccKalmanFilter";

    private double m_timeStampMsPredict;
    private double m_timeStampMsUpdate;
    private int m_predictCount;
    private KalmanFilter m_kf;
    private double m_accXSigma;
    private  double m_accYSigma;
    private boolean m_useGpsSpeed;
    private double mVelFactor = 1.0;
    private double mPosFactor = 1.0;
    private boolean m_useZUPT = false;

    public GPSAccEKF(boolean useGpsSpeed,
                              double x,
                              double y,
                              double xVel,
                              double yVel,
                              double accXDev,
                              double accYDev,
                              double posDev,
                              double timeStampMs,
                              double velFactor,
                              double posFactor,
                              boolean useZUPT) {
        int mesDim = useGpsSpeed ? 4 : 2;
        m_useGpsSpeed = useGpsSpeed;
        m_useZUPT = useZUPT;

        m_kf = new KalmanFilter(4, mesDim, 1);
        m_timeStampMsPredict = m_timeStampMsUpdate = timeStampMs;
        m_accXSigma = accXDev;
        m_accYSigma = accYDev;
        m_predictCount = 0;
        m_kf.Xk_k.setData(x, y, xVel, yVel);

        m_kf.H.setIdentityDiag(); //state has 4d and measurement has 4d too. so here is identity
        m_kf.Pk_k.setIdentity();
        m_kf.Pk_k.scale(posDev);
        m_kf.Pk_k.scale(1);
        mVelFactor = velFactor;
        mPosFactor = posFactor;
    }

    private void rebuildF(double dtPredict, double zuptStatus) {
        double f[] = {
                1.0, 0.0, dtPredict, 0.0,
                0.0, 1.0, 0.0, dtPredict,
                0.0, 0.0, zuptStatus, 0.0,
                0.0, 0.0, 0.0, zuptStatus
        };
        m_kf.F.setData(f);
    }

    private void rebuildU(double xAcc,
                          double yAcc) {
        m_kf.Uk.setData(xAcc, yAcc);
    }

    private void rebuildB(double dtPredict) {
        double dt2 = 0.5*dtPredict*dtPredict;
        double b[] = {
                dt2, 0.0,
                0.0, dt2,
                dtPredict, 0.0,
                0.0, dtPredict
        };
        m_kf.B.setData(b);
    }

    private void rebuildR(double posSigma, double velSigma) {

        posSigma *= mPosFactor;
        velSigma *= mVelFactor;

        Log.i(TAG, "rebuildR: { " +
                "velSigma : " + velSigma +
                ", posSigma : " + posSigma +
                ", velFactor : " + mVelFactor +
                ", posFactor :" + mPosFactor +
                "}");
        if (m_useGpsSpeed) {
            double R[] = {
                    posSigma, 0.0, 0.0, 0.0,
                    0.0, posSigma, 0.0, 0.0,
                    0.0, 0.0, velSigma, 0.0,
                    0.0, 0.0, 0.0, velSigma
            };
            m_kf.R.setData(R);
        } else {
            m_kf.R.setIdentity();
            m_kf.R.scale(posSigma);
        }
    }


       /* private void rebuildQ(double dtUpdate,
                              double accDev) {
    //        now we use predictCount. but maybe there is way to use dtUpdate.
    //        m_kf.Q.setIdentity();
    //        m_kf.Q.scale(accSigma * dtUpdate);
            double velDev = accDev * m_predictCount;
            double posDev = velDev * m_predictCount / 2;
            double covDev = velDev * posDev;

            double posSig = posDev * posDev;
            double velSig = velDev * velDev;

            double Q[] = {
                    posSig, 0.0, covDev, 0.0,
                    0.0, posSig, 0.0, covDev,
                    0.0, 0.0, velSig, 0.0,
                    0.0, 0.0, 0.0, velSig
            };
            m_kf.Q.setData(Q);
        }
        */

    private void rebuildQ(double dtUpdate,
                          double accXDev,
                          double accYDev) {
        double velXDev = accXDev * dtUpdate;
        double velYDev = accYDev * dtUpdate;
        double posXDev = velXDev * dtUpdate / 2;
        double posYDev = velYDev * dtUpdate / 2;
        double Q[] = {
                posXDev * posXDev,  0.0,                posXDev * velXDev,      0.0,
                0.0,                posYDev * posYDev,  0.0,                    posYDev * velYDev,
                posXDev * velXDev,  0.0,                velXDev * velXDev,      0.0,
                0.0,                posYDev * velYDev,  0.0,                    velYDev * velYDev
        };

        m_kf.Q.setData(Q);

    }

    public void predict(double timeNowMs,
                        double xAcc,
                        double yAcc,
                        boolean zuptStatus) {
        double dtPredict = (timeNowMs - m_timeStampMsPredict) / 1000.0;
        double dtUpdate = (timeNowMs - m_timeStampMsUpdate) / 1000.0;


        if(m_useZUPT == true){
            rebuildF(dtPredict, zuptStatus == true?1:0);
        }
        else
        {
            rebuildF(dtPredict, 1);
        }
        rebuildB(dtPredict);
        rebuildU(xAcc, yAcc);

        ++m_predictCount;
        rebuildQ(dtUpdate, m_accXSigma, m_accYSigma);
        //rebuildQ(dtPredict, m_accXSigma, m_accYSigma);

        m_timeStampMsPredict = timeNowMs;
        m_kf.predict();
        Matrix.matrixCopy(m_kf.Xk_km1, m_kf.Xk_k);
    }

    public void update(double timeStamp,
                       double x,
                       double y,
                       double xVel,
                       double yVel,
                       double posDev,
                       double velErr) {
        m_predictCount = 0;
        m_timeStampMsUpdate = timeStamp;
        rebuildR(posDev, velErr);
        m_kf.Zk.setData(x, y, xVel, yVel);
        m_kf.update();
    }

    public double getCurrentX() {
        return m_kf.Xk_k.data[0][0];
    }

    public double getCurrentY() {
        return m_kf.Xk_k.data[1][0];
    }

    public double getCurrentXVel() {
        return m_kf.Xk_k.data[2][0];
    }

    public double getCurrentYVel() {
        return m_kf.Xk_k.data[3][0];
    }

}

