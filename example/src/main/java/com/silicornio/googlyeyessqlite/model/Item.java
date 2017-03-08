package com.silicornio.googlyeyessqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Item {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public boolean flag;

    @DatabaseField
    public String text;

    @DatabaseField
    public int numberInt;

    @DatabaseField
    public float numberFloat;

    @DatabaseField
    public double numberDouble;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (flag != item.flag) return false;
        if (numberInt != item.numberInt) return false;
        if (Float.compare(item.numberFloat, numberFloat) != 0) return false;
        if (Double.compare(item.numberDouble, numberDouble) != 0) return false;
        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        return text != null ? text.equals(item.text) : item.text == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (flag ? 1 : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + numberInt;
        result = 31 * result + (numberFloat != +0.0f ? Float.floatToIntBits(numberFloat) : 0);
        temp = Double.doubleToLongBits(numberDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", flag=" + flag +
                ", text='" + text + '\'' +
                ", numberInt=" + numberInt +
                ", numberFloat=" + numberFloat +
                ", numberDouble=" + numberDouble +
                '}';
    }
}
