package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.FileSerializable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

public class Posting extends AbstractPosting implements FileSerializable {
    /**
     * 缺省构造函数
     */
    public Posting() {
    }

    public Posting(int docId, int freq, List<Integer> positions) {
        super(docId, freq, positions);
    }

    /**
     * 判断二个Posting内容是否相同
     *
     * @param obj ：要比较的另外一个Posting
     * @return 如果内容相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Posting) {
            if (this.docId != ((Posting) obj).docId) return false;
            if (this.freq != ((Posting) obj).freq) return false;
            if (this.positions.size() != ((Posting) obj).positions.size()) return false;
            for (int i : ((Posting) obj).positions)
                if (!this.positions.contains(i)) return false;
            return true;
        }
        return false;
    }

    /**
     * 返回Posting的字符串表示
     *
     * @return 字符串
     */
    @Override
    public String toString() {

        return "Posting Info: " + "docId: " + this.docId + " " + "freq: " + this.freq + " " + positions.toString();
    }

    /**
     * 返回包含单词的文档id
     *
     * @return ：文档id
     */
    public int getDocId() {
        return this.docId;
    }

    /**
     * 设置包含单词的文档id
     *
     * @param docId：包含单词的文档id
     */
    public void setDocId(int docId) {
        this.docId = docId;
    }

    /**
     * 返回单词在文档里出现的次数
     *
     * @return ：出现次数
     */
    public int getFreq() {
        return this.freq;
    }

    /**
     * 设置单词在文档里出现的次数
     *
     * @param freq:单词在文档里出现的次数
     */
    public void setFreq(int freq) {
        this.freq = freq;
    }

    /**
     * 返回单词在文档里出现的位置列表
     *
     * @return ：位置列表
     */
    public List<Integer> getPositions() {
        return this.positions;
    }

    /**
     * 设置单词在文档里出现的位置列表
     *
     * @param positions：单词在文档里出现的位置列表
     */
    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    /**
     * 比较二个Posting对象的大小（根据docId）
     *
     * @param o： 另一个Posting对象
     * @return ：二个Posting对象的docId的差值
     */
    @Override
    public int compareTo(AbstractPosting o) {
        return this.docId - ((Posting) o).docId;
    }

    /**
     * 对内部positions从小到大排序
     */
    public void sort() {
        Collections.sort(positions);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.getDocId());
            out.writeObject(this.getFreq());
            out.writeObject(this.getPositions().size());
            for (int i : this.getPositions())
                out.writeObject(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            this.setDocId((int) in.readObject());
            this.setFreq((int) in.readObject());
            int tmpSize = (int) in.readObject();
            for (int i = 0; i < tmpSize; i++)
                this.getPositions().add((int) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
