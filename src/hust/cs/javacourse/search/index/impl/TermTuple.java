package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;

public class TermTuple extends AbstractTermTuple {
    /**
     * 缺省构造函数
     */
    public TermTuple() {
    }

    /**
     * 判断二个三元组内容是否相同
     *
     * @param obj ：要比较的另外一个三元组
     * @return 如果内容相等（三个属性内容都相等）返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj)
            throws IllegalArgumentException {
        if (obj instanceof TermTuple)
            return this.term.getContent().equals(((TermTuple) obj).term.getContent()) && this.curPos == ((TermTuple) obj).curPos;
        else throw new IllegalArgumentException("Not TermTuple Object");
    }

    /**
     * 获得三元组的字符串表示
     *
     * @return ： 三元组的字符串表示
     */
    @Override
    public String toString() {
        return "TermTuple Info: "
                + "term: " + this.term.getContent() + " "
                + "freq: " + this.freq + " "
                + "curPos: " + this.curPos;
    }
}
