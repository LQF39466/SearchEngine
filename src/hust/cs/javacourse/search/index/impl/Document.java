package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractTermTuple;

import java.util.List;

public class Document extends AbstractDocument {

    public Document() {
    }

    public Document(int docId, String docPath) {
        super(docId, docPath);
    }

    public Document(int docId, String docPath, List<AbstractTermTuple> tuples) {
        super(docId, docPath, tuples);
    }

    /**
     * 获得文档id
     *
     * @return ：文档id
     */
    public int getDocId() {
        return this.docId;
    }

    /**
     * 设置文档id
     *
     * @param docId：文档id
     */
    public void setDocId(int docId) {
        this.docId = docId;
    }

    /**
     * 获得文档绝对路径
     *
     * @return ：文档绝对路径
     */
    public String getDocPath() {
        return this.docPath;
    }

    /**
     * 设置文档绝对路径
     *
     * @param docPath：文档绝对路径
     */
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    /**
     * 获得文档包含的三元组列表
     *
     * @return ：文档包含的三元组列表
     */
    public List<AbstractTermTuple> getTuples() {
        return this.tuples;
    }

    /**
     * 向文档对象里添加三元组, 不会重复添加同一个三元组
     *
     * @param tuple ：要添加的三元组
     */
    public void addTuple(AbstractTermTuple tuple) {
        for (AbstractTermTuple tmp : this.tuples)
            if (tmp.equals(tuple)) return;
        this.tuples.add(tuple);
    }

    /**
     * 判断是否包含指定的三元组
     *
     * @param tuple ： 指定的三元组
     * @return ： 如果包含指定的三元组，返回true;否则返回false
     */
    public boolean contains(AbstractTermTuple tuple) {
        for (AbstractTermTuple tmp : this.tuples)
            if (tmp.equals(tuple)) return true;
        return false;
    }

    /**
     * 获得指定下标位置的三元组
     *
     * @param index：指定下标位置
     * @return ：三元组
     */
    public AbstractTermTuple getTuple(int index) {
        return this.tuples.get(index);
    }

    /**
     * 返回文档对象包含的三元组的个数
     *
     * @return ：文档对象包含的三元组的个数
     */
    public int getTupleSize() {
        return this.tuples.size();
    }

    /**
     * 获得Document的字符串表示
     *
     * @return ： Document的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder(this.docId + ", " + this.docPath);
        for (AbstractTermTuple i : tuples)
            tmp.append(", ").append(i.toString());
        return tmp.toString();
    }
}