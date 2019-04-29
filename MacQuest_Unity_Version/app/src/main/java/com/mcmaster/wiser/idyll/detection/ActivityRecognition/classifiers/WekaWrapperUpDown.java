// Generated with Weka 3.8.1
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Thu Apr 06 17:25:06 EDT 2017

package com.mcmaster.wiser.idyll.detection.ActivityRecognition.classifiers;

import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

public class WekaWrapperUpDown
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

        return WekaClassifierUpDown.classify(s);
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
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.1).\n" + this.getClass().getName() + "/WekaClassifierUpDown";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    public static void main(String args[]) {
        runClassifier(new WekaWrapperUpDown(), args);
    }
}

class WekaClassifierUpDown {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifierUpDown.N4ef7fb5a112(i);
        return p;
    }
    static double N4ef7fb5a112(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 0.5264034) {
            p = WekaClassifierUpDown.N5b1d0e6f113(i);
        } else if (((Double) i[0]).doubleValue() > 0.5264034) {
            p = WekaClassifierUpDown.N7c452917129(i);
        }
        return p;
    }
    static double N5b1d0e6f113(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -0.5989295) {
            p = WekaClassifierUpDown.N2e2d27bc114(i);
        } else if (((Double) i[0]).doubleValue() > -0.5989295) {
            p = WekaClassifierUpDown.N48e7cf40122(i);
        }
        return p;
    }
    static double N2e2d27bc114(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 1.6852802) {
            p = WekaClassifierUpDown.N25985511115(i);
        } else if (((Double) i[1]).doubleValue() > 1.6852802) {
            p = WekaClassifierUpDown.N67995542117(i);
        }
        return p;
    }
    static double N25985511115(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 0.7160882) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 0.7160882) {
            p = WekaClassifierUpDown.N544457d3116(i);
        }
        return p;
    }
    static double N544457d3116(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -1.7225435) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > -1.7225435) {
            p = 0;
        }
        return p;
    }
    static double N67995542117(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= -1.8119322) {
            p = WekaClassifierUpDown.N204e8eca118(i);
        } else if (((Double) i[0]).doubleValue() > -1.8119322) {
            p = WekaClassifierUpDown.N1c0b2ad5121(i);
        }
        return p;
    }
    static double N204e8eca118(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 3.3665943) {
            p = WekaClassifierUpDown.N4799770f119(i);
        } else if (((Double) i[1]).doubleValue() > 3.3665943) {
            p = 0;
        }
        return p;
    }
    static double N4799770f119(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= -2.7584558) {
            p = WekaClassifierUpDown.N31954ae0120(i);
        } else if (((Double) i[0]).doubleValue() > -2.7584558) {
            p = 2;
        }
        return p;
    }
    static double N31954ae0120(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 2.9297364) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 2.9297364) {
            p = 2;
        }
        return p;
    }
    static double N1c0b2ad5121(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 7.0147376) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 7.0147376) {
            p = 1;
        }
        return p;
    }
    static double N48e7cf40122(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 1.3934456) {
            p = WekaClassifierUpDown.N444af85c123(i);
        } else if (((Double) i[1]).doubleValue() > 1.3934456) {
            p = WekaClassifierUpDown.N3befbfcc128(i);
        }
        return p;
    }
    static double N444af85c123(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= -2.5318098) {
            p = WekaClassifierUpDown.N5f93787f124(i);
        } else if (((Double) i[1]).doubleValue() > -2.5318098) {
            p = 0;
        }
        return p;
    }
    static double N5f93787f124(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -5.5257688) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > -5.5257688) {
            p = WekaClassifierUpDown.N1a8bacda125(i);
        }
        return p;
    }
    static double N1a8bacda125(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -0.35030955) {
            p = WekaClassifierUpDown.N6ce0fd04126(i);
        } else if (((Double) i[0]).doubleValue() > -0.35030955) {
            p = 0;
        }
        return p;
    }
    static double N6ce0fd04126(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -0.51051533) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > -0.51051533) {
            p = WekaClassifierUpDown.N7f7b70dc127(i);
        }
        return p;
    }
    static double N7f7b70dc127(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= -0.37370166) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > -0.37370166) {
            p = 2;
        }
        return p;
    }
    static double N3befbfcc128(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 4.4330654) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 4.4330654) {
            p = 1;
        }
        return p;
    }
    static double N7c452917129(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.5501069) {
            p = WekaClassifierUpDown.N17a6f346130(i);
        } else if (((Double) i[1]).doubleValue() > 0.5501069) {
            p = WekaClassifierUpDown.N337f8cfe131(i);
        }
        return p;
    }
    static double N17a6f346130(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 2.0492976) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 2.0492976) {
            p = 1;
        }
        return p;
    }
    static double N337f8cfe131(Object[]i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 0.7530863) {
            p = WekaClassifierUpDown.N75285482132(i);
        } else if (((Double) i[0]).doubleValue() > 0.7530863) {
            p = 1;
        }
        return p;
    }
    static double N75285482132(Object[]i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 2.576171) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 2.576171) {
            p = 1;
        }
        return p;
    }
}