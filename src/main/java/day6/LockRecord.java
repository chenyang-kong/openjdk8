package day6;

public class LockRecord {
    private  MarkWord markWord;

    private MarkWord owner;

    public LockRecord(MarkWord markWord, MarkWord owner) {
        this.markWord = markWord;
        this.owner = owner;
    }

    public MarkWord getMarkWord() {
        return markWord;
    }

    public void setMarkWord(MarkWord markWord) {
        this.markWord = markWord;
    }

    public MarkWord getOwner() {
        return owner;
    }

    public void setOwner(MarkWord owner) {
        this.owner = owner;
    }
}
