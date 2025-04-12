# Collektive Compiler Plugin

This compiler plugin is responsible for managing the alignment of aggregate computation.

## Alignment processing strategy

The alignment processing pursues the following strategy: in the first instance all the functions definition are visited
and the ones involving aggregate computation will be subject to alignment processing.
Then, for each candidate function, the plugin visits all the call sites in the body of the function and checks if the
call has an aggregate reference or if it is involved in an aggregate computation. If so, the plugin will align the
expression call.
During the visiting of the function definition, branch conditions are also visited aligning only the branches that
involve aggregate computation. If a branch body do not involve aggregate computation, the plugin will not align it.
Aligning the branches in this way, by default all the branched follow the `branch` semantics of AC.

### Alignment strategy formalisation

1. Each function definition exhibiting the following characteristics is target of the alignment processing:
    * The function has an `extensionReceiver` of type `Aggregate` or a subtype of it.
    * The function has a `dispatchReceiver` of type `Aggregate` or a subtype of it.
    * One or more of the function's parameters are of type `Aggregate` or a subtype of it.
2. For each candidate function, aligns the call expressions having an aggregate reference or in depth they involve an
   aggregate computation.