package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    public void open(String indexFile) {
        index.load(new File(indexFile));
        index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList postingList = index.search(queryTerm);
        if (postingList == null) return null;
        List<AbstractHit> hits = new ArrayList<>();
        for (int i = 0; i < postingList.size(); i++) {
            AbstractPosting posting = postingList.get(i);
            AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()), new TreeMap<>());
            hit.getTermPostingMapping().putIfAbsent(queryTerm, posting);
            sorter.score(hit);
            hits.add(hit);
        }
        sorter.sort(hits);
        return hits.toArray(new AbstractHit[0]);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        if (postingList1 == null && postingList2 == null) return null;
        List<AbstractHit> hits = new ArrayList<>();
        switch (combine) {
            case AND:
                if (postingList1 == null || postingList2 == null) return null;
                for (int i = 0; i < postingList1.size(); i++) {
                    AbstractPosting posting = postingList1.get(i);
                    int indexOfList2;
                    if ((indexOfList2 = postingList2.indexOf(posting.getDocId())) == -1) continue; //这一步筛选掉只出现了Term1的文档
                    AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()));
                    hit.getTermPostingMapping().putIfAbsent(queryTerm1, posting);
                    hit.getTermPostingMapping().putIfAbsent(queryTerm2, postingList2.get(indexOfList2));
                    sorter.score(hit);
                    hits.add(hit);
                }
                sorter.sort(hits);
                return hits.toArray(new AbstractHit[0]);
            case OR:
                if(postingList1 == null)return search(queryTerm2,sorter);
                else if(postingList2 == null)return search(queryTerm1,sorter);//只有一个词有搜索结果的情况，转化为单个词语的检索
                for (int i = 0; i < postingList1.size(); i++) {
                    AbstractPosting posting = postingList1.get(i);
                    AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()));
                    hit.getTermPostingMapping().putIfAbsent(queryTerm1, posting);
                    int indexTmp = postingList2.indexOf(posting.getDocId());
                    if (indexTmp != -1) { //说明这个文档中也出现了queryTerm2
                        hit.getTermPostingMapping().putIfAbsent(queryTerm2, postingList2.get(indexTmp));
                        postingList2.remove(indexTmp);//从List2中移除这个文档，防止被重复添加
                    }
                    sorter.score(hit);
                    hits.add(hit);
                }
                for (int i = 0; i < postingList2.size(); i++) { //再将只出现在List2中的文档加进来
                    AbstractPosting posting = postingList2.get(i);
                    AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()));
                    hit.getTermPostingMapping().putIfAbsent(queryTerm2, posting);
                    sorter.score(hit);
                    hits.add(hit);
                }
                sorter.sort(hits);
                return hits.toArray(new AbstractHit[0]);
        }
        return null;
    }

    /**
     * 根据二个检索词组成的短语进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter) {
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        if (postingList1 == null || postingList2 == null) return null;
        List<AbstractHit> hits = new ArrayList<>();
        for (int i = 0; i < postingList1.size(); i++) {
            AbstractPosting posting = postingList1.get(i);
            int indexOfList2; //用来记录List2中匹配结果的索引便于复用
            if ((indexOfList2 = postingList2.indexOf(posting.getDocId())) == -1) continue; //这一步筛选掉只出现了Term1的文档
            AbstractPosting phrasePosting = new Posting(posting.getDocId(), 0, new ArrayList<>()); //新建一个Posting用来写入结果
            for (int j : posting.getPositions()) {//Term1在文档1中出现在位置j
                if (postingList2.get(indexOfList2).getPositions().contains(j + 1)) {//查找Term2是否出现在位置j+1
                    phrasePosting.setFreq(phrasePosting.getFreq() + 1);
                    phrasePosting.getPositions().add(j);
                }
            }
            if (phrasePosting.getFreq() == 0) continue;
            AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()));
            hit.getTermPostingMapping().putIfAbsent(queryTerm1, phrasePosting);
            sorter.score(hit);
            hits.add(hit);
        }
        sorter.sort(hits);
        return hits.toArray(new AbstractHit[0]);
    }
}
