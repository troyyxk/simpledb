package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * Stores actual field info
     * */
    private List<TDItem> fieldList;

    /**
     * Maps field name to its index in fieldList
     * */
    private Map<String, Integer> nameIdxMap;

    /**
     * Size of the tuple in bytes
     * */
    private int size;

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // done
        return fieldList.iterator();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // done
        fieldList = new ArrayList<>();
        nameIdxMap = new HashMap<>();
        size = 0;
        int fieldNum = typeAr.length;
        for (int i = 0; i < fieldNum; i++) {
            fieldList.add(new TDItem(typeAr[i], fieldAr[i]));
            if (fieldAr[i] != null) {
                nameIdxMap.put(fieldAr[i], i);
            }
            size += typeAr[i].getLen();
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // done
        fieldList = new ArrayList<>();
        nameIdxMap = new HashMap<>();
        size = 0;
        for (Type type : typeAr) {
            fieldList.add(new TDItem(type, null));
            size += type.getLen();
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // done
        return fieldList.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        //TODO: i 1-index or 0-index
        // done
        if (i >= numFields() || i < 0) {
            throw new NoSuchElementException();
        }
        return fieldList.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // done
        if (i >= numFields() || i < 0) {
            throw new NoSuchElementException();
        }
        return fieldList.get(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // done
        if (!nameIdxMap.containsKey(name)) {
            throw new NoSuchElementException();
        }
        return nameIdxMap.get(name);
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // done
        return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // done
        int length1 = td1.numFields(), length2 = td2.numFields();
        Type[] typeAr = new Type[length1 + length2];
        String[] fieldAr = new String[length1 + length2];
        int idx = 0;
        for (int i = 0; i < length1; i++) {
            typeAr[idx] = td1.getFieldType(i);
            fieldAr[idx++] = td1.getFieldName(i);
        }
        for (int i = 0; i < length2; i++) {
            typeAr[idx] = td2.getFieldType(i);
            fieldAr[idx++] = td2.getFieldName(i);
        }
        return new TupleDesc(typeAr, fieldAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        //TODO: Equality criterion may vary
        if (!(o instanceof TupleDesc)) {
            return false;
        }
        TupleDesc compDesc = (TupleDesc)o;
        int thisNumFields = numFields(), compNumFields = compDesc.numFields();
        if (size != compDesc.getSize() || thisNumFields != compNumFields) {
            return false;
        }
        for (int i = 0; i < compNumFields; i++) {
            if (!getFieldType(i).equals(compDesc.getFieldType(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        return fieldList.hashCode();
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
         StringBuilder sb = new StringBuilder();
         for (TDItem field : fieldList) {
             sb.append(field.fieldType);
             sb.append('(');
             sb.append(field.fieldName);
             sb.append("), ");
         }
         sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
