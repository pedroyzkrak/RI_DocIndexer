/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class MetricsData implements Comparable<MetricsData> {

    private int docId;
    private double score;


    /**
     * Contructor that supports information about query results to calculate evaluation and efficiency metrics
     *
     * @param docId the query ID
     * @param score   document score
     */
    public MetricsData(int docId, double score) {
        this.docId = docId;
        this.score = score;
    }

    public int getDocId() {
        return docId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.docId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetricsData other = (MetricsData) obj;
        return this.docId == other.docId;
    }

    /**
     * Comparable method to sort a list of MetricsData objects by decreasing order of document score
     */
    @Override
    public int compareTo(MetricsData md) {
        if (this.score < md.score)
            return 1;
        else if (md.score < this.score)
            return -1;

        return 0;
    }

}
