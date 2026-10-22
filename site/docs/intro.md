---
sidebar_position: 0
---

# Collektive Documentation

:::warning
This documentation is still a work in progress, 
some sections may be incomplete or missing,
sorry for the inconvenience.

If you find any bugs or suggestions, 
please feel free to open an issue or fix them through a [PR](https://github.com/Collektive/collektive/pulls) :)
:::

import {Card, CardGrid} from '@site/src/components/Cards';

Collektive is a Kotlin Multiplatform framework for **aggregate programming**:
you write one program for a whole network of devices, and Collektive turns it into what each device computes and sends.

## Start here

<CardGrid columns={3}>
  <Card to="/docs/introduction/what-is-collektive" icon="compass" eyebrow="Introduction" title="What is Collektive?">
    The problems it solves, and what makes it different.
  </Card>
  <Card to="/docs/tutorials/gettingstarted" icon="rocket" eyebrow="Tutorial" title="Getting Started">
    Add Collektive and its compiler plugin to a Gradle project.
  </Card>
  <Card to="/docs/tutorials/first-collektive-program" icon="code" eyebrow="Tutorial" title="Your first program">
    A self-healing collective, step by step.
  </Card>
</CardGrid>

## Learn

<CardGrid columns={2}>
  <Card to="/docs/category/tutorials" icon="learn" title="Tutorials">
    Basic usage, common mistakes, and simulation with Alchemist and Unity.
  </Card>
  <Card to="/docs/explanation/fields" icon="layers" title="Thinking in Fields">
    The mental model of aggregate programming: fields, neighbors, and rounds.
  </Card>
  <Card to="/docs/explanation/dsl-descriptions" icon="puzzle" title="Aggregate Operators">
    `evolve`, `neighboring`, `share`, `exchange`, and how to choose among them.
  </Card>
  <Card to="/docs/explanation/stdlib-descriptions" icon="book-open" title="Standard Library">
    Self-stabilizing building blocks: distances, broadcasts, collection, gossip, leaders, time.
  </Card>
  <Card to="/docs/explanation/alignment" icon="sitemap" title="Alignment and Partitioning">
    How messages find their operator, and why branches split the network.
  </Card>
  <Card to="/docs/explanation/compiler-plugin" icon="magic" title="The Compiler Plugin">
    Alignment at compile time, and the diagnostics that catch misuses of the DSL.
  </Card>
</CardGrid>

## Reference

<CardGrid columns={3}>
  <Card to="/docs/glossary" icon="book" title="Glossary">
    The terms of aggregate programming, in one place.
  </Card>
  <Card to="/docs/references" icon="code" title="APIs and examples">
    API documentation, examples, and template projects.
  </Card>
  <Card to="/docs/category/showcase" icon="flask" title="Showcase">
    Research built with Collektive, with experiments and artifacts.
  </Card>
</CardGrid>

[//]: # (How-to guides: hidden for now, see /docs/category/how-to-guides/)
