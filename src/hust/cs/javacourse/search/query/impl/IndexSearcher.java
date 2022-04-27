package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.*;

public class IndexSearcher extends AbstractIndexSearcher{
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     * @param indexFile ：指定索引文件
     */
    public void open(String indexFile){
        index.load(new File(indexFile));
        index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     * @param queryTerm ：检索词
     * @param sorter ：排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter){
        AbstractPostingList postingList = index.search(queryTerm);
        List<AbstractHit> hits = new ArrayList<>();
        for(int i = 0; i < postingList.size(); i++){
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
     *
     * 根据二个检索词进行搜索
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter ：    排序器
     * @param combine ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine){
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        List<AbstractHit> hits = new ArrayList<>();
        switch (combine) {
            case AND:
                for (int i = 0; i < postingList1.size(); i++) {
                    AbstractPosting posting = postingList1.get(i);
                    if (postingList2.indexOf(posting.getDocId()) == -1) continue;
                    AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()));
                    hit.getTermPostingMapping().putIfAbsent(queryTerm1, posting);
                    sorter.score(hit);
                    hits.add(hit);
                }
                sorter.sort(hits);
                return hits.toArray(new AbstractHit[0]);
            case OR:
                for (int i = 0; i < postingList1.size(); i++) {
                    AbstractPosting posting = postingList1.get(i);
                    AbstractHit hit = new Hit(posting.getDocId(), index.docIdToDocPathMapping.get(posting.getDocId()));
                    hit.getTermPostingMapping().putIfAbsent(queryTerm1, posting);
                    int indexTmp = postingList2.indexOf(posting.getDocId());
                    if (indexTmp != -1) { //说明这个文档中也出现了queryTerm2
                        hit.getTermPostingMapping().putIfAbsent(queryTerm2, postingList2.get(indexTmp));
                        postingList2.remove(indexTmp);
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
}
