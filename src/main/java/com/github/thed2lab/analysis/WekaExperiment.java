package com.github.thed2lab.analysis;


import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

// opencsv
import com.opencsv.CSVWriter;

import net.bytebuddy.jar.asm.Attribute;
// weka.jar
import weka.core.Instances;
import weka.core.Range;
import weka.core.Utils;

// weka classifiers
import weka.classifiers.*;
import weka.classifiers.bayes.*;
import weka.classifiers.meta.*;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.*;
import weka.classifiers.misc.*;
import weka.classifiers.rules.*;
import weka.classifiers.trees.*;

// weka experiment
import weka.experiment.*;

import javax.swing.*;

public class WekaExperiment {
    
    public void run() {
        
    }

    private static Classifier[] getClassificationClassifiers() throws Exception {
		Classifier[] classifiers = new Classifier[46];

		// set baseline classifier here
		classifiers[0] = new ZeroR();

		// bayes
		classifiers[1] = new BayesNet();
		classifiers[2] = new NaiveBayes();
		classifiers[3] = new NaiveBayesMultinomialText();
		classifiers[4] = new NaiveBayesUpdateable();
        classifiers[5] = new NaiveBayesMultinomial();
        classifiers[6] = new NaiveBayesMultinomialUpdateable();

		// functions
		classifiers[7] = new Logistic();
		classifiers[8] = new MultilayerPerceptron();
		classifiers[9] = new SGD();
		classifiers[10] = new SGDText();
		classifiers[11] = new SimpleLogistic();
		classifiers[12] = new SMO();
		classifiers[13] = new VotedPerceptron();

		// lazy
		classifiers[14] = new IBk();
		classifiers[15] = new KStar();
		classifiers[16] = new LWL();

		// meta classifiers
		classifiers[17] = new AdaBoostM1();
		classifiers[18] = new AttributeSelectedClassifier();
		classifiers[19] = new Bagging();
		classifiers[20] = new ClassificationViaRegression();
		classifiers[21] = new CVParameterSelection();
		classifiers[22] = new FilteredClassifier();
		classifiers[23] = new IterativeClassifierOptimizer();
		classifiers[24] = new LogitBoost();
		classifiers[25] = new MultiClassClassifier();
		classifiers[26] = new MultiClassClassifierUpdateable();
		classifiers[27] = new MultiScheme();
		classifiers[28] = new RandomCommittee();
		classifiers[29] = new RandomizableFilteredClassifier();
		classifiers[30] = new RandomSubSpace();
		classifiers[31] = new Stacking();
		classifiers[32] = new Vote();
		classifiers[33] = new WeightedInstancesHandlerWrapper();

		// Misc
		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setOptions(Utils.splitOptions("-M"));
		classifiers[34] = imc;

		// Rules
		classifiers[35] = new DecisionTable();
		classifiers[36] = new JRip();
		classifiers[37] = new OneR();
		classifiers[38] = new PART();

        // Tree
		classifiers[39] = new DecisionStump();
		classifiers[40] = new HoeffdingTree();
		classifiers[41] = new J48();
		classifiers[42] = new LMT();
		classifiers[43] = new RandomForest();
		classifiers[44] = new RandomTree();
		classifiers[45] = new REPTree();
        //classifiers[46] = new DPCTree(); 

		return classifiers;
	}

    private static Classifier[] getRegressionClassifiers() {
		Classifier[] classifiers = new Classifier[20];

		// set baseline classifier here
		classifiers[0] = new ZeroR();

		// functions
		classifiers[1] = new GaussianProcesses();
		classifiers[2] = new LinearRegression();
		classifiers[3] = new MultilayerPerceptron();
		classifiers[4] = new SimpleLinearRegression();
		classifiers[5] = new SMOreg();

		// meta
		classifiers[6] = new Bagging();
		classifiers[7] = new CVParameterSelection();
		classifiers[8] = new RegressionByDiscretization();

		classifiers[9] = new MultiScheme();
		classifiers[10] = new RandomCommittee();
		classifiers[11] = new RandomizableFilteredClassifier();
		classifiers[12] = new RandomSubSpace();
		classifiers[13] = new Stacking();
		classifiers[14] = new Vote();
		classifiers[15] = new WeightedInstancesHandlerWrapper();

		// rules
		classifiers[16] = new DecisionTable();
		classifiers[17] = new M5Rules();

		// trees
		classifiers[18] = new M5P();
		classifiers[19] = new REPTree();

        // misc
        // classifiers[20] = new ElasticNet();
        // classifiers[21] = new IsotonicRegression();
        // classifiers[22] = new LeastMedSq();
        classifiers[20] = new IBk();
        classifiers[21] = new KStar();
        classifiers[22] = new LWL();
        classifiers[23] = new DecisionStump();
        // classifiers[27] = new DPCTree();
        classifiers[24] = new RandomForest();
        classifiers[25] = new AdditiveRegression();
        // classifiers[30] = new IterativeAbsoluteErrorRegression();
        classifiers[26] = new AttributeSelectedClassifier();

		return classifiers;
	}
}
