package com.mcmaster.wiser.idyll.presenter;

public interface QRScanResultReceiver {

    public void QRScanResultData(String codeFormat, String codeContent);

    public void QRScanResultData(NoScanResultException noScanData);

}
