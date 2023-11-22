package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

public class LengthTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public LengthTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     *
     * @return 下一个三元组；按长度过滤，如果到了流的末尾，返回null
     */
    public AbstractTermTuple next() {
        AbstractTermTuple tmp;
        do {
            tmp = input.next();
            if (tmp == null) return null;
        } while (tmp.term.getContent().length() < Config.TERM_FILTER_MINLENGTH || tmp.term.getContent().length() > Config.TERM_FILTER_MAXLENGTH);
        return tmp;
    }
}
