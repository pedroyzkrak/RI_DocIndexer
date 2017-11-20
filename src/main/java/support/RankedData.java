package support;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * Saves ranked search information
 */
public class RankedData {
    private String term;
    private double weight;
    private double score;

    /**
     * Contructor that supports a term and it's term frequency
     *
     * @param term   query object
     * @param weight Weight of term in the document
     */
    public RankedData(String term, double weight) {
        this.term = term;
        this.weight = weight;
        this.score = 0;
    }

    public String getTerm() {
        return term;
    }

    public double getWeight() {
        return weight;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RankedData that = (RankedData) o;

        return term.equals(that.term);
    }

    @Override
    public int hashCode() {
        return term.hashCode();
    }
}
