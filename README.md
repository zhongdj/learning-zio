# learning-zio

By upgrading git-stats-backend to a new stage, a production level engineering quality is required. 

To separate technical and business complexity, a side-effect free style and library is a key tool to 'application-core' layer (by onion architecture).
Scala is a great programming language with outstanding productivity, mastering FP and OO at the same time is awesome, but what would be the proper programming model at application core is weathy to try with zio, 
which at least helps align types/monads/effects and separates effectful declaration and implementations.

# Features
As is shown in following picture, these features will be provided by this project:

![读模型](docs/read_models.png)

# Architecture
Logically, the architecture is shown as following picture:

![架构](docs/modules.jpg)

# Concepts
- <b> Analytic Project </b>,
each <b>Analytic Project</b> contains many <b>Git Projects</b>. 

- <b> Git Project </b>,
each <b> Git Project </b> relates many <b> Analytic Tasks </b>. 
each <b> Git Project </b> has basically 3 attributes:
   * repositoryUrl, such as "ssh://zhongdj@github.com/zhongdj/git-stats"
   * branch, such as master
   * local, such as true or false
each <b> Git Project </b> has basicall 2 options:
   * language, such as scala or golang
   * excludes, indicates which files will not be analyzed, such as vendor dir for golang
- <b> Analytic Task </b>,
each <b> Analytic Task </b> has many <b> Analytic Task Items</b>, and each <b> Analytic Task </b> corresponds one of the following categories:
   * organisation
   * iteration
   * design basics 
   * coding basics

- <b> Analytic Task Item </b> corresponds a concrete read model, such as contributor analysis or onion iterate rythm analysis and so on.
   * Organisation
      * Contributor By Period
      * Commit Message Violation
      * Contributor Hierarchy
   * Iteration
      * Onion Architecture Iterating Rythm Chaos
      * Isunami Commit
      * Incremental Cost
      * Incremental Cost Trend
   * Design basic
      * Stability Ranking
      * SDP Violation
   * Coding basic
      * Duplicated Code
      * Effective Code Contribution
      * Risky Functions
      * Cyclomatic Complexity Violation
      * Cyclomatic Complexity Trend
      * Long Functions Violation
      * Long Functions Trend
      * Long Arguments List Violation
      * Long Arguments List Trend

# Use Cases
