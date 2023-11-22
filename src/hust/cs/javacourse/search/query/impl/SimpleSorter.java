package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.Sort;

import java.util.Collections;
import java.util.List;

public class SimpleSorter implements Sort {
    /**
     * 对命中结果集合根据文档得分排序
     *
     * @param hits ：命中结果集合
     */
    public void sort(List<AbstractHit> hits) {
        Collections.sort(hits);
    }

    /**
     * <pre>
     * 计算命中文档的得分, 作为命中结果排序的依据.
     *      这个SimpleSorter类的打分策略可以同时实现单个Term打分和多个Term打分
     *      单个Term打分时，该Term出现频率越高的文件得分越高
     *      多个Term打分时，按照Term数量优先，文件中出现的不同Term越多得分越高，Term数相同的情况下，再比较词频
     * @param hit ：命中文档
     * @return ：命中文档的得分
     * </pre>
     */
    public double score(AbstractHit hit) {
        double result = 0;
        int count = 1;
        for (AbstractTerm i : hit.getTermPostingMapping().keySet()) {
            double tmp = hit.getTermPostingMapping().get(i).getFreq();
            tmp = Math.exp(-tmp);
            tmp *= (-100000); //这两行是把freq的正整数值域映射到-100000-0的自然数域内
            tmp += count++ * 100000; //将上述域变成正数，同时保证出现更多个词的文件得分一定更高
            result += tmp;
        }
        hit.setScore(-result);
        return -result;
    }
}
