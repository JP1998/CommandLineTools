package de.hotzjeanpierre.commandlinetools.command.testutilities;

public class SomeClass {

    protected int i;
    protected double j;

    public SomeClass(int i, double j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SomeClass) {
            return ((SomeClass) obj).i == i && ((SomeClass) obj).j == j;
        }
        return false;
    }
}