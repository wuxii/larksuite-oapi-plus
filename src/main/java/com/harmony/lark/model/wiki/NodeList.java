package com.harmony.lark.model.wiki;

import com.harmony.lark.Node;
import com.harmony.lark.model.ListResult;
import lombok.Data;

public class NodeList extends ListResult<Node> {

    @Data
    public static class Node {

        private Boolean hasChild;

        private String title;

    }

}
