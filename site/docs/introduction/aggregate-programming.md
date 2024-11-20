---
title: Aggregate Programming 
---

# The Challenge of Maintaining Distributed Systems 

The **progressive increase** in the number of interconnected devices, leads to a **rise in the costs** associated with maintaining distributed systems. This creates **significant challenges** in implementing software services on a **global scale** using the traditional approach of individually programming each agent. This situation drives the search for solutions aimed at improving the **autonomy** of computing systems and **reducing their complexity**.

## Aggregate Programming as a Relevant Approach

In this context, **aggregate programming** emerges as a relevant approach. It is based on the **functional composition** of **reusable collective behavior blocks**, with the goal of efficiently achieving **complex** and **resilient behaviors** in **dynamic networks**.

### Key Properties of Aggregate Programming

- **Self-stabilization**
- **Independence from density**

These properties are initially studied on **basic building blocks**, such as:
- **Broadcasting**
- **Distance estimation**
- **Data aggregation**

These building blocks are then transferred and validated on more **complex systems** created through **functional composition**.

Aggregate programming finds its **conceptual roots** in **Field Calculus** and presents itself as a minimal functional programming language designed for the specification and composition of collective behaviors.

## Levels of Abstraction in Aggregate Programming

Through multiple levels of abstraction[<sup>1</sup>](#bibliography), aggregate programming mitigates the complexity involved in distributed coordination within IoT network environments:

<div className="centered">
  ![Aggregated levels of abstraction in aggregate programming](/img/multiple-levels-of-abstraction-ag.png)
  <p>Aggregated levels of abstraction in aggregate programming</p>
</div>

### Field Calculus Constructs

This layer, depicted as the second layer in the figure, represents the interface where aggregate programming interacts with the external environment, composed of device infrastructure and non-aggregated software services, which together form the system's lowest layer.

A key abstraction provided by **Field Calculus** is based on the notion of a *field*, a conceptualization inspired by physical phenomena. In this view, every networked device is mapped to a local value within a field. For example, temperature sensors create a field of ambient temperature values, smartphone accelerometers contribute to a field of movement directions, and notification applications generate a field of messages displayed on mobile devices.

Field manipulation and creation are achieved through four main constructs:

1. **Functions**: Expressed as `b(e1, ..., en)`, these apply a function `b` to arguments `e1, ..., en`. These functions include mathematical, logical, and algorithmic built-ins, as well as user-defined functions, and may represent sensors or actuators.

2. **State variables (`rep`)**: The construct `rep(x ← v) s1; ...; sn` introduces a local state variable `x`, initialized with `v` and periodically updated by executing the block `s1; ...; sn`. This enables the definition of fields that dynamically evolve over time.

3. **Neighbor values (`nbr`)**: The operation `nbr(s)` collects the latest values from all neighboring devices (including the device itself). Functions like `minHood(m)` synthesize these values, for example, by determining the minimum value in the field `m`.

4. **Conditional branching (`if-else`)**: The construct `if(e) s1; ...; sn else s1'; ...; sm'` divides the network into two regions. In regions where the expression `e` evaluates as true, `s1; ...; sn` is executed, while `s1'; ...; sm'` is executed in other regions. Importantly, the branches remain separate and do not interact.

### Resilient coordination operators

The next level of abstraction within the aggregate programming framework introduces resilience and identifies a set of general basic operators designed for use in resilient coordination applications. This layer, positioned at the center in figure, includes coordination mechanisms that exhibit self-stabilization properties, i.e., the ability to adapt reactively to changes in the network structure or input values.

A possible collection of basic operators includes three generalized coordination operators, along with "if" and built-in functions. The three operators are:

1. **G(source, init, metric, accumulate)**: This operation represents a "spreading" operation that generalizes distance measurement, broadcasting, and projection operations. It performs two main tasks: first, it calculates a field of minimum distances from a specified source region (represented by a boolean field, `source`) using a provided metric. Then, it propagates values along the resulting distance gradient, starting from an initial value (`init`) and accumulating further values along the gradient using an accumulation function (`accumulate`).

2. **C(potential, accumulate, local, null)**: This operation is responsible for accumulating information towards the source along the gradient of a potential field. Starting from an idempotent neutral element (`null`), the local value is combined with the "uphill" values using a commutative and associative accumulation function (`accumulate`), resulting in a cumulative value at the source.

3. **T(initial, floor, decay)**: This operation describes a flexible countdown process characterized by a rate that may change over time. The "decay" function progressively reduces the initial value (`initial`) until it reaches the minimum allowed value (`floor`).

### Developer APIs

Libraries developed using basic operators can employ and combine these operators to create a pragmatic and user-friendly **API** (Application Programming Interface). These libraries represent the penultimate layer in the structure illustrated in figure, on which application code is based. 

For example, many actions and information diffusion functions in distributed environments can rely on the **G** operation, such as:

```javascript
// Estimating the distance from one or more designated source devices
def distanceTo(source) {
  G(source, 0, () -> {nbrRange},
  (v) -> {v + nbrRange})
}
```

The use of APIs facilitates aggregate programming by providing tools for implementing sophisticated behaviors within dynamic networks.

## Bibliography

1. Beal, Viroli, and Pianini. Aggregate Programming for the Internet of Things.


