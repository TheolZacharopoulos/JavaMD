package schemas.runtime_diagram.runtime_nodes;

import helpers.FactoriesProvider;
import schemas.runtime_diagram.tokens.Token;
import schemas.static_diagram.nodes.InitialNode;

import java.util.ArrayList;
import java.util.List;

public interface RuntimeInitialNode extends RuntimeControlNode, InitialNode {

    default void fire(List<Token> tokens) {
        List<Token> producedTokens = new ArrayList<Token>();
        producedTokens.add(FactoriesProvider.getTokenFactory().ControlToken());

        addTokens(producedTokens);
        sendOffers(producedTokens);
    }

    default boolean isReady() {
        return false; // is fired by activity directly
    }
}
