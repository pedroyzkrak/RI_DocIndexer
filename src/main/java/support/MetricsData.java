/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

/**
 *
 * @author Pedro
 */
public class MetricsData implements Comparable<MetricsData> {

    private int docId;
    private double score;

    public MetricsData(int queryId, double score) {
        this.docId = queryId;
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
        if (this.docId != other.docId) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(MetricsData md) {
        if (this.score < md.score) {
            return 1;
        } else if (md.score < this.score) {
            return -1;
        }
        return 0;
    }

}
