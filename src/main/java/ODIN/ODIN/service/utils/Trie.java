package ODIN.ODIN.service.utils;

import ODIN.base.common.constants.Constants;
import ODIN.base.domain.GlobalVariable;

/**
 * Trie tree
 * 2022/4/15 zhoutao
 */
public class Trie {
    private Trie[] children;
    private boolean isEnd;

    public Trie() {
        children = new Trie[GlobalVariable.BRANCH];
        isEnd = false;
    }

    /**
     * insert Trie tree
     *
     * @param clusterName clusterName
     */
    public void insert(String clusterName) {
        Trie node = this;
        String[] str = clusterName.split(Constants.CLUSTER_NAME_SUFFIX);
        for (int i = 0; i < str.length; i++) {
            int branchIndex = Integer.parseInt(str[i]);
            if (node.children[branchIndex] == null) {
                node.children[branchIndex] = new Trie();
            }
            node = node.children[branchIndex];
        }
        node.isEnd = true;
    }

    /**
     * Trie tree contains clusterName
     *
     * @param clusterName clusterName
     * @return result
     */
    public boolean isProcessed(String clusterName) {
        Trie node = this;
        String[] str = clusterName.split(Constants.CLUSTER_NAME_SUFFIX);

        for (int i = 0; i < str.length; i++) {
            int branchIndex = Integer.parseInt(str[i]);
            if (node.children[branchIndex] == null) {
                return false;
            }
            if (node.children[branchIndex].isEnd) {
                return true;
            }
            node = node.children[branchIndex];
        }
        return node.isEnd;
    }
}
