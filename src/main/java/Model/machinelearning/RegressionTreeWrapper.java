package Model.machinelearning;

import Model.*;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegressionTreeWrapper {

    private REPTree repTree;
    private Instances fullDataset;
    private Evaluation evaluation;

    public RegressionTreeWrapper(String pathDatasetFile, Boolean buildWithTrainingSetAndTestSet, boolean balanceDate) {
        repTree = new REPTree();

        // caricamento del dataset
        CSVLoader csvLoader = new CSVLoader();
        try {
            csvLoader.setSource(new File(pathDatasetFile));
            fullDataset = csvLoader.getDataSet();

            // stampa del dataset
            // System.out.println(dataset.toString());

            // setting della variabile dipendente
            fullDataset.setClassIndex(fullDataset.numAttributes()-1);

            // ten cross validation ha mostrato che l'intero dataset su cui sarà addestrato il modello dà buoni risultati
            // quindi è preferibile addestrarlo sull'intero dataset e non su una parte di esso
            if(buildWithTrainingSetAndTestSet)
                buildModelWithTrainingSetAndTestSet(67, 33, balanceDate);
            else
                buildModelWithTenFoldsCrossValidation(balanceDate);
        } catch (IOException e) {
            throw new RuntimeException("File csv non trovato" + e);
        } catch (Exception e){
            throw new RuntimeException("Problemi nella costruzione del modello [WEKA]" + e);
        }

    }

    private void buildModelWithTrainingSetAndTestSet(double percentTrain, double percentTest, boolean balanceDate) throws Exception {

        // randomizza fullDataset
        Randomize randomize = new Randomize();
        randomize.setInputFormat(fullDataset);
        fullDataset = Filter.useFilter(fullDataset, randomize);

        // ottiene il training set dal 67% del fullDataset
        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setPercentage(percentTrain);
        removePercentage.setInputFormat(fullDataset);
        Instances trainingSet = Filter.useFilter(fullDataset, removePercentage);
        trainingSet.setClassIndex(trainingSet.numAttributes() -1);

        // ottiene il test set dal 33% del fullDataset
        removePercentage.setInvertSelection(true);
        removePercentage.setPercentage(percentTest);
        removePercentage.setInputFormat(fullDataset);
        Instances testSet = Filter.useFilter(fullDataset, removePercentage);
        testSet.setClassIndex(testSet.numAttributes() -1);

        // bilanciamento dati di training
        ClassBalancer classBalancer = (ClassBalancer) createClassBalancer(trainingSet);
        Filter.useFilter(trainingSet, classBalancer);

        // addestramento regressore
        repTree.buildClassifier(trainingSet);

        // valutazione metriche
        if(balanceDate) {
            evaluation = new Evaluation(trainingSet);
            evaluation.evaluateModel(repTree, testSet);
        }

        // output valutazioni
        System.out.println("TopClothesRT evaluate with Training Set (" + percentTrain + ")%" + "and Test Set (" +
                percentTest + "%):\n" + evaluation.toSummaryString());
    }

    private void buildModelWithTenFoldsCrossValidation(boolean balanceDate) throws Exception{

        // valutiamo quanto buono sarà il regressore mediante 10 folds cross validation
        evaluation = new Evaluation(fullDataset);
        evaluation.crossValidateModel(repTree, fullDataset, 10, new Random(1));

        // output valutazioni
        System.out.println("TopClothesRT evaluate with Ten Folds Cross Validation:\n"+ evaluation.toSummaryString());


        // bilanciamento dati di training
        if(balanceDate) {
            ClassBalancer classBalancer = (ClassBalancer) createClassBalancer(fullDataset);
            Filter.useFilter(fullDataset, classBalancer);
        }
        // addestramento regressore
        repTree.buildClassifier(fullDataset);
    }

    // il filtro si occuperà di ponderare le istanze del dataset sia che vi siano variabili numeriche che nominali
    // in modo che abbiano lo stesso peso, facendo undersampling e oversampling
    private Filter createClassBalancer(Instances trainingSet) throws Exception {
        ClassBalancer classBalancer = new ClassBalancer();
        classBalancer.setInputFormat(trainingSet);
        return classBalancer;
    }


    public String getEvaluation(){
        return (evaluation != null) ? evaluation.toSummaryString(): "null";
    }


    public List<ScoreCapoAbbigliamento> classifyInstances(List<CapoAbbigliamento> capoAbbigliamentoList,
                                                          MeteoInformation meteoInformation, String stagione){
        List<Instance> listInstance = new ArrayList<>();

        for (CapoAbbigliamento capoAbbigliamento: capoAbbigliamentoList){
            Instance instance = new DenseInstance(capoAbbigliamento.getClass() != Scarpa.class ? 7 : 8);
            instance.setDataset(fullDataset);
            if(capoAbbigliamento.getClass() == Maglia.class || capoAbbigliamento.getClass() == Pantalone.class){
                instance.setValue(0, capoAbbigliamento.getMateriale());
                instance.setValue(1, capoAbbigliamento.getColore());

                if(capoAbbigliamento.getClass() == Maglia.class)
                    instance.setValue(2, ((Maglia)capoAbbigliamento).getLunghezzaManica());
                else
                    instance.setValue(2,((Pantalone)capoAbbigliamento).getLunghezza() );

                instance.setValue(3, capoAbbigliamento.getStagione());
                instance.setValue(4, meteoInformation.getMeteo());
                instance.setValue(5, meteoInformation.getTemperaturaPercepita());
                instance.setValue(6, stagione);
            }else{
                Scarpa scarpa = (Scarpa) capoAbbigliamento;

                instance.setValue(0, scarpa.getTipo());
                instance.setValue(1, scarpa.getAntiscivolo()? 1 : 0);
                instance.setValue(2, scarpa.getImpermeabile()? 1 : 0);
                instance.setValue(3, scarpa.getColore());
                instance.setValue(4, scarpa.getStagione());
                instance.setValue(5, meteoInformation.getMeteo());
                instance.setValue(6, meteoInformation.getTemperaturaPercepita());
                instance.setValue(7, stagione);
            }

            listInstance.add(instance);
        }

        List<ScoreCapoAbbigliamento> scoreCapoAbbigliamentoList = new ArrayList<>();

        int i = 0;
        for(Instance instance: listInstance) {
            try{
                double predict = repTree.classifyInstance(instance);
                ScoreCapoAbbigliamento scoreCapoAbbigliamento = new ScoreCapoAbbigliamento(capoAbbigliamentoList.get(i), predict);
                scoreCapoAbbigliamentoList.add(scoreCapoAbbigliamento);
                i++;
            }catch (Exception e){
                throw new RuntimeException("Predict blocked on " + i + "instance" + e);
            }

        }

        return scoreCapoAbbigliamentoList;
    }

    public List<ScoreCapoAbbigliamento> getBestThreeTopClothes(List<ScoreCapoAbbigliamento> scoreCapoAbbigliamentoList){
        List<ScoreCapoAbbigliamento> bests = new ArrayList<>();
        Comparatore comparatore = new Comparatore();

        for(int i=0; i<3; i++){
            ScoreCapoAbbigliamento scoreCapoAbbigliamentoMax = Collections.max(scoreCapoAbbigliamentoList, comparatore);
            bests.add(scoreCapoAbbigliamentoMax);
            scoreCapoAbbigliamentoList.remove(scoreCapoAbbigliamentoMax);
        }
        return bests;
    }

    private static class Comparatore implements Comparator<ScoreCapoAbbigliamento>{
        @Override
        public int compare(ScoreCapoAbbigliamento o1, ScoreCapoAbbigliamento o2) {
            return o1.getPunteggio().compareTo(o2.getPunteggio());
        }
    }

}