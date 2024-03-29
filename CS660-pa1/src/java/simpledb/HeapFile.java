
package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 *
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private File dataFile;

    private TupleDesc td;

    private int id;

    /**
     * Constructs a heap file backed by the specified file.
     *
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.dataFile = f;
        this.td = td;
        this.id = f.getAbsolutePath().hashCode();
    }

    /**
     * Returns the File backing this HeapFile on disk.
     *
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return dataFile;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     *
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        return id;
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     *
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(dataFile, "r");
            int offSet = pid.pageNumber() * BufferPool.getPageSize();
            byte[] data = new byte[BufferPool.getPageSize()];
            randomAccessFile.seek(offSet);
            randomAccessFile.readFully(data);
            randomAccessFile.close();
            return new HeapPage((HeapPageId) pid, data);
        }
        catch (FileNotFoundException e) {
            System.err.println("data file invalid in heap file");
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            System.err.println("data file io failed");
            e.printStackTrace();
            return null;
        }
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int)Math.floor((double) dataFile.length() / BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new AbstractDbFileIterator() {

            int currentPageIdx;

            Iterator<Tuple> pageIt;

            @Override
            protected Tuple readNext() throws DbException, TransactionAbortedException {
                if (pageIt == null) {
                    return null;
                }
                if (pageIt.hasNext()) {
                    return pageIt.next();
                }
                else {
                    if (currentPageIdx < numPages() - 1) {
                        currentPageIdx++;
                        pageIt = ((HeapPage) Database.getBufferPool().getPage(tid, new HeapPageId(id, currentPageIdx),Permissions.READ_ONLY)).iterator();
                        return pageIt.hasNext() ? pageIt.next() : null;
                    }
                    else {
                        return null;
                    }
                }
            }

            @Override
            public void open() throws DbException, TransactionAbortedException {
                currentPageIdx = 0;
                pageIt = ((HeapPage) Database.getBufferPool().getPage(tid, new HeapPageId(getId(), currentPageIdx),Permissions.READ_ONLY)).iterator();
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                open();
            }

            @Override
            public void close() {
                super.close();
                pageIt = null;
                currentPageIdx = -1;
            }
        };
    }

}

