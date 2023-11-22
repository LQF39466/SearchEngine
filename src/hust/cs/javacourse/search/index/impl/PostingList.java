package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class PostingList extends AbstractPostingList {
    /**
     * 缺省构造函数
     */
    public PostingList() {
    }

    /**
     * 添加Posting,不会重复添加同样的Posting
     *
     * @param posting：Posting对象
     */
    public void add(AbstractPosting posting) {
        for (AbstractPosting tmp : this.list)
            if (tmp.equals(posting)) return;
        this.list.add(posting);
    }

    /**
     * 获得PosingList的字符串表示
     *
     * @return ： PosingList的字符串表示
     */
    @Override
    public String toString() {
        return list.toString();
    }

    /**
     * 添加Posting列表,不会重复添加同样的Posting
     *
     * @param postings：Posting列表
     */
    public void add(List<AbstractPosting> postings) {
        for (AbstractPosting tmp1 : postings) {
            boolean flag = false;
            for (AbstractPosting tmp2 : this.list)
                if (tmp2.equals(tmp1)) {
                    flag = true;
                    break;
                }
            if (flag) continue;
            this.list.add(tmp1);
        }
    }

    /**
     * 返回指定下标位置的Posting
     *
     * @param index ：下标
     * @return : 指定下标位置的Posting
     */
    public AbstractPosting get(int index) {
        return this.list.get(index);
    }

    /**
     * 返回指定Posting对象的下标
     *
     * @param posting：指定的Posting对象
     * @return ：如果找到返回对应下标；否则返回-1
     */
    public int indexOf(AbstractPosting posting) {
        for (int i = 0; i < this.list.size(); i++)
            if (this.list.get(i).equals(posting)) return i;
        return -1;
    }

    /**
     * 返回指定文档id的Posting对象的下标
     *
     * @param docId ：文档id
     * @return ：如果找到返回对应下标；否则返回-1
     */
    public int indexOf(int docId) {
        for (int i = 0; i < this.list.size(); i++)
            if (this.list.get(i).getDocId() == docId) return i;
        return -1;
    }

    /**
     * 是否包含指定Posting对象
     *
     * @param posting： 指定的Posting对象
     * @return : 如果包含返回true，否则返回false
     */
    public boolean contains(AbstractPosting posting) {
        for (AbstractPosting tmp : this.list)
            if (tmp.equals(posting)) return true;
        return false;
    }

    /**
     * 删除指定下标的Posting对象
     *
     * @param index：指定的下标
     */
    public void remove(int index) {
        this.list.remove(index);
    }

    /**
     * 删除指定的Posting对象
     *
     * @param posting ：定的Posting对象
     */
    public void remove(AbstractPosting posting) {
        for (int i = 0; i < this.list.size(); i++)
            if (this.list.get(i).equals(posting)) {
                remove(i);
                return;
            }
    }

    /**
     * 返回PostingList的大小，即包含的Posting的个数
     *
     * @return ：PostingList的大小
     */
    public int size() {
        return this.list.size();
    }

    /**
     * 清除PostingList
     */
    public void clear() {
        this.list.clear();
    }

    /**
     * PostingList是否为空
     *
     * @return 为空返回true;否则返回false
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /**
     * 根据文档id的大小对PostingList进行从小到大的排序
     */
    public void sort() {
        for (int i = 0; i < this.list.size() - 1; i++) {
            for (int j = i + 1; j < this.list.size(); j++) {
                if (this.list.get(i).getDocId() > this.list.get(j).getDocId()) {
                    AbstractPosting tmp = this.list.get(i);
                    this.list.set(i, this.list.get(j));
                    this.list.set(j, tmp);
                }
            }
        }
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(list.size());
            for (AbstractPosting i : list)
                i.writeObject(out);
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
            int tmpSize = (int) in.readObject();
            for (int i = 0; i < tmpSize; i++) {
                AbstractPosting tmp = new Posting();
                tmp.readObject(in);
                list.add(tmp);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
