/*
 * BEACH-Gaze is open-source software issued under the GNU General Public License.
 */
package com.github.lab.analysis;

import weka.classifiers.*;

public class ClassifierResult {
    private Classifier classifier;
    private Double score;
    private int significance;
    private String classifierCol;

    // Need to verify if classifier assigned is accurate, I don't think it is
    public ClassifierResult(Classifier classifier, Double score, int significance, String classifierCol) {
        this.classifier = classifier;
        this.score = score;
        this.significance = significance;
        this.classifierCol = classifierCol;
    }

    // public Classifier getClassifier() {
    //     return classifier;
    // }

    // public String getClassifierName() {
    //     return classifier.getClass().getSimpleName();
    // }

    public Double getScore() {
        return score;
    }

    public int getSignificance() {
        return significance;
    }

    public String getClassifierCol() {
        return classifierCol;
    }
}
