// Generated with Weka 3.8.1
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sun Apr 02 23:51:33 EDT 2017

package com.mcmaster.wiser.idyll.detection.ActivityRecognition.classifiers;

import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WekaWrapperWalkStand
        extends AbstractClassifier {

    /**
     * Returns only the toString() method.
     *
     * @return a string describing the classifier
     */
    public String globalInfo() {
        return toString();
    }

    /**
     * Returns the capabilities of this classifier.
     *
     * @return the capabilities
     */
    public Capabilities getCapabilities() {
        weka.core.Capabilities result = new weka.core.Capabilities(this);

        result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);


        result.setMinimumNumberInstances(0);

        return result;
    }

    /**
     * only checks the data against its capabilities.
     *
     * @param i the training data
     */
    public void buildClassifier(Instances i) throws Exception {
        // can classifier handle the data?
        getCapabilities().testWithFail(i);
    }

    /**
     * Classifies the given instance.
     *
     * @param i the instance to classify
     * @return the classification result
     */
    public double classifyInstance(Instance i) throws Exception {
        Object[] s = new Object[i.numAttributes()];

        for (int j = 0; j < s.length; j++) {
            if (!i.isMissing(j)) {
                if (i.attribute(j).isNominal())
                    s[j] = new String(i.stringValue(j));
                else if (i.attribute(j).isNumeric())
                    s[j] = new Double(i.value(j));
            }
        }

        // set class value to missing
        s[i.classIndex()] = null;

        return WekaClassifier.classify(s);
    }

    /**
     * Returns the revision string.
     *
     * @return        the revision
     */
    public String getRevision() {
        return RevisionUtils.extract("1.0");
    }

    /**
     * Returns only the classnames and what classifier it is based on.
     *
     * @return a short description
     */
    public String toString() {
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.1).\n" + this.getClass().getName() + "/WekaClassifier";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    public static void main(String args[]) {
        runClassifier(new WekaWrapperWalkStand(), args);
    }
}

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N718b7ee00(i);
        return p;
    }
    static double N718b7ee00(Object[]i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 0;
        } else if (((Double) i[26]).doubleValue() <= 25.27499) {
            p = WekaClassifier.N1fd76f421(i);
        } else if (((Double) i[26]).doubleValue() > 25.27499) {
            p = WekaClassifier.N29b24fcf3(i);
        }
        return p;
    }
    static double N1fd76f421(Object[]i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() <= 0.18477502) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() > 0.18477502) {
            p = WekaClassifier.N7137c50d2(i);
        }
        return p;
    }
    static double N7137c50d2(Object[]i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 1.0) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 1.0) {
            p = 0;
        }
        return p;
    }
    static double N29b24fcf3(Object[]i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 24.0) {
            p = WekaClassifier.N36448674(i);
        } else if (((Double) i[20]).doubleValue() > 24.0) {
            p = 0;
        }
        return p;
    }
    static double N36448674(Object[]i) {
        double p = Double.NaN;
        if (i[25] == null) {
            p = 0;
        } else if (((Double) i[25]).doubleValue() <= 9.300272) {
            p = WekaClassifier.N27ed8a365(i);
        } else if (((Double) i[25]).doubleValue() > 9.300272) {
            p = WekaClassifier.N534ada947(i);
        }
        return p;
    }
    static double N27ed8a365(Object[]i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if (((Double) i[8]).doubleValue() <= 9.30691) {
            p = WekaClassifier.N683d2c6b6(i);
        } else if (((Double) i[8]).doubleValue() > 9.30691) {
            p = 0;
        }
        return p;
    }
    static double N683d2c6b6(Object[]i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = 0;
        } else if (((Double) i[28]).doubleValue() <= 2.4171925) {
            p = 0;
        } else if (((Double) i[28]).doubleValue() > 2.4171925) {
            p = 1;
        }
        return p;
    }
    static double N534ada947(Object[]i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 1;
        } else if (((Double) i[26]).doubleValue() <= 34.496254) {
            p = WekaClassifier.N72cc97ea8(i);
        } else if (((Double) i[26]).doubleValue() > 34.496254) {
            p = WekaClassifier.N6abf355f21(i);
        }
        return p;
    }
    static double N72cc97ea8(Object[]i) {
        double p = Double.NaN;
        if (i[35] == null) {
            p = 0;
        } else if (((Double) i[35]).doubleValue() <= 0.2506727) {
            p = WekaClassifier.N39bb1fbf9(i);
        } else if (((Double) i[35]).doubleValue() > 0.2506727) {
            p = WekaClassifier.N106322f015(i);
        }
        return p;
    }
    static double N39bb1fbf9(Object[]i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() <= 0.13216431) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() > 0.13216431) {
            p = WekaClassifier.N3864229710(i);
        }
        return p;
    }
    static double N3864229710(Object[]i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 1.1591767) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 1.1591767) {
            p = WekaClassifier.N3d7fc17511(i);
        }
        return p;
    }
    static double N3d7fc17511(Object[]i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 4.0) {
            p = WekaClassifier.N2b1a62f712(i);
        } else if (((Double) i[5]).doubleValue() > 4.0) {
            p = 1;
        }
        return p;
    }
    static double N2b1a62f712(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 1.0) {
            p = WekaClassifier.N4a3dd6cc13(i);
        } else if (((Double) i[1]).doubleValue() > 1.0) {
            p = 0;
        }
        return p;
    }
    static double N4a3dd6cc13(Object[]i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 0.18438959) {
            p = WekaClassifier.N5354258614(i);
        } else if (((Double) i[12]).doubleValue() > 0.18438959) {
            p = 0;
        }
        return p;
    }
    static double N5354258614(Object[]i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() <= -0.018652435) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() > -0.018652435) {
            p = 1;
        }
        return p;
    }
    static double N106322f015(Object[]i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 0.116053954) {
            p = WekaClassifier.N6816e7d116(i);
        } else if (((Double) i[10]).doubleValue() > 0.116053954) {
            p = WekaClassifier.N4cb9c7a917(i);
        }
        return p;
    }
    static double N6816e7d116(Object[]i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() <= 0.014457645) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() > 0.014457645) {
            p = 1;
        }
        return p;
    }
    static double N4cb9c7a917(Object[]i) {
        double p = Double.NaN;
        if (i[30] == null) {
            p = 1;
        } else if (((Double) i[30]).doubleValue() <= 1.4193044) {
            p = 1;
        } else if (((Double) i[30]).doubleValue() > 1.4193044) {
            p = WekaClassifier.N3eb3b3ad18(i);
        }
        return p;
    }
    static double N3eb3b3ad18(Object[]i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 2.0) {
            p = WekaClassifier.N374e722919(i);
        } else if (((Double) i[5]).doubleValue() > 2.0) {
            p = 0;
        }
        return p;
    }
    static double N374e722919(Object[]i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() <= 0.4334918) {
            p = WekaClassifier.N6839337e20(i);
        } else if (((Double) i[11]).doubleValue() > 0.4334918) {
            p = 1;
        }
        return p;
    }
    static double N6839337e20(Object[]i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 7.0) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 7.0) {
            p = 1;
        }
        return p;
    }
    static double N6abf355f21(Object[]i) {
        double p = Double.NaN;
        if (i[29] == null) {
            p = 1;
        } else if (((Double) i[29]).doubleValue() <= 2.1001546) {
            p = WekaClassifier.N49b2cd7e22(i);
        } else if (((Double) i[29]).doubleValue() > 2.1001546) {
            p = WekaClassifier.Nd29215b23(i);
        }
        return p;
    }
    static double N49b2cd7e22(Object[]i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= 0.4326614) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() > 0.4326614) {
            p = 0;
        }
        return p;
    }
    static double Nd29215b23(Object[]i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 0.23146732) {
            p = WekaClassifier.N2c2a414324(i);
        } else if (((Double) i[11]).doubleValue() > 0.23146732) {
            p = 1;
        }
        return p;
    }
    static double N2c2a414324(Object[]i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 3.0) {
            p = WekaClassifier.N3fac8c5425(i);
        } else if (((Double) i[4]).doubleValue() > 3.0) {
            p = WekaClassifier.N5a13c4d628(i);
        }
        return p;
    }
    static double N3fac8c5425(Object[]i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 2.0) {
            p = WekaClassifier.N60aa6fe226(i);
        } else if (((Double) i[4]).doubleValue() > 2.0) {
            p = WekaClassifier.N24cda95527(i);
        }
        return p;
    }
    static double N60aa6fe226(Object[]i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 0.21034664) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() > 0.21034664) {
            p = 0;
        }
        return p;
    }
    static double N24cda95527(Object[]i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() <= -0.08345422) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() > -0.08345422) {
            p = 1;
        }
        return p;
    }
    static double N5a13c4d628(Object[]i) {
        double p = Double.NaN;
        if (i[32] == null) {
            p = 1;
        } else if (((Double) i[32]).doubleValue() <= 9.773678) {
            p = WekaClassifier.N1862dec29(i);
        } else if (((Double) i[32]).doubleValue() > 9.773678) {
            p = 1;
        }
        return p;
    }
    static double N1862dec29(Object[]i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 1;
        } else if (((Double) i[20]).doubleValue() <= 12.0) {
            p = WekaClassifier.N32e0874130(i);
        } else if (((Double) i[20]).doubleValue() > 12.0) {
            p = WekaClassifier.N3f5f599e32(i);
        }
        return p;
    }
    static double N32e0874130(Object[]i) {
        double p = Double.NaN;
        if (i[12] == null) {
            p = 1;
        } else if (((Double) i[12]).doubleValue() <= -0.18500048) {
            p = WekaClassifier.N7bae174931(i);
        } else if (((Double) i[12]).doubleValue() > -0.18500048) {
            p = 1;
        }
        return p;
    }
    static double N7bae174931(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 8.0) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 8.0) {
            p = 0;
        }
        return p;
    }
    static double N3f5f599e32(Object[]i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = 0;
        } else if (((Double) i[28]).doubleValue() <= 5.0903406) {
            p = WekaClassifier.Nf86ead433(i);
        } else if (((Double) i[28]).doubleValue() > 5.0903406) {
            p = WekaClassifier.N39eaaeea34(i);
        }
        return p;
    }
    static double Nf86ead433(Object[]i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 1;
        } else if (((Double) i[14]).doubleValue() <= -0.0739851) {
            p = 1;
        } else if (((Double) i[14]).doubleValue() > -0.0739851) {
            p = 0;
        }
        return p;
    }
    static double N39eaaeea34(Object[]i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 1;
        } else if (((Double) i[18]).doubleValue() <= 13.0) {
            p = 1;
        } else if (((Double) i[18]).doubleValue() > 13.0) {
            p = WekaClassifier.N32f5e2c135(i);
        }
        return p;
    }
    static double N32f5e2c135(Object[]i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 0;
        } else if (((Double) i[18]).doubleValue() <= 15.0) {
            p = 0;
        } else if (((Double) i[18]).doubleValue() > 15.0) {
            p = 1;
        }
        return p;
    }
}