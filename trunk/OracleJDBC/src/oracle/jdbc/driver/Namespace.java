package oracle.jdbc.driver;

class Namespace {
    static final int ATTRIBUTE_MAX_LENGTH = 30;
    static final int VALUE_MAX_LENGTH = 4000;
    String name;
    boolean clear;
    String[] keys;
    String[] values;
    int nbPairs;

    Namespace(String _name) {
        if (_name == null) {
            throw new NullPointerException();
        }
        this.name = _name;
        this.clear = false;
        this.nbPairs = 0;
        this.keys = new String[5];
        this.values = new String[5];
    }

    void clear() {
        this.clear = true;

        for (int i = 0; i < this.nbPairs; i++) {
            this.keys[i] = null;
            this.values[i] = null;
        }
        this.nbPairs = 0;
    }

    void setAttribute(String attribute, String value) {
        if ((attribute == null) || (value == null) || (attribute.equals(""))) {
            throw new NullPointerException();
        }
        if (this.nbPairs == this.keys.length) {
            String[] keys1 = new String[this.keys.length * 2];
            String[] values1 = new String[this.keys.length * 2];
            System.arraycopy(this.keys, 0, keys1, 0, this.keys.length);
            System.arraycopy(this.values, 0, values1, 0, this.values.length);
            this.keys = keys1;
            this.values = values1;
        }
        this.keys[this.nbPairs] = attribute;
        this.values[this.nbPairs] = value;
        this.nbPairs += 1;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.Namespace JD-Core Version: 0.6.0
 */