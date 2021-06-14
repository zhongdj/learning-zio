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
   * excludes, files needs to be ignored in all metrics, such as generated files or imported source code (i.e. vendor dir for golang) 

each <b> Git Project </b> has basicaly 2 options:
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

## Create an analytic task 

### with default configuration 
```shell
curl -H "Content-Type:application/json" \
  http://${analytic.host}:${port}/v1/analyticProjects \
  -d \
  '{
    "gitProjects" : [{
      "repositoryUrl" : "git@github.com:zhongdj/git-stats-backend.git",
      "branch"        : "master"
    }]
  }'
```

### with options
- interval, which indicates delay in seconds between two git project clone commands
- repeatedCycle, which indicates the project will be re-run incrementally in specified minutes  

```shell
curl -H "Content-Type:application/json" \
  http://${analytic.host}:${port}/v1/analyticProjects \
  -d \
  '{
    "gitProjects" : [
      {
        "repositoryUrl" : "git@github.com:zhongdj/git-stats-backend.git",
        "branch"        : "master",
        "local"         : false,
        "excludes"      : ["LICENSE", "project/target"]
      }
    ],
    "options" : {
      "interval"        : 20 
      "repeatedCycle"   : 240  
    }
  }'
```

 Note: w/o <b>Analytic Task Item Configuration</b>, the following read models should be generated
 
 | Category   |       Read Model            | Enabled | Comments                                                  | 
 | :--------- | :--------------------       | :----- | -------                                                    |
 |Organisation| Contributor By Period       | √      |                                                            |
 |Organisation| Commit Message Violation    | x      |  commit message patterns need to be specified               |
 |Organisation| Contributor Hierarchy       | x      |  team hierarchy need to be specified separately             |
 |Iteration   | Onion Architecture Rhythm   | x      |  onion tag file need to be specified                         |
 |Iteration   | Isunami Commit              | √      |  by default 500 loc change is the valve for each commit    |
 |Iteration   | Incremental Cost            | √ or x |  depends on if language specific dependency analysis tool ready or not   |
 |Iteration   | Incremental Cost Trend      | √ or x |  same as above                                             |
 |Design      | Stability Ranking           | √ or x |  same as above                                             |
 |Design      | SDP Violation               | √ or x |  same as above                                             |
 |Coding      | Duplicated Code             | √ or x |  same as above                                             |
 |Coding      | Effective Code Contribution | √ or x |  same as above                                             |
 |Coding      | Risky Functions             | √ or x |  same as above                                             |
 |Coding      | Cyclomatic Complexity       | √ or x |  same as above                                             |
 |Coding      | Cyclomatic Complexity Trend | √ or x |  same as above                                             |
 |Coding      | Long Functions              | √ or x |  same as above                                             |
 |Coding      | Long Functions Complexity   | √ or x |  same as above                                             |
 |Coding      | Long Arguments List         | √ or x |  same as above                                             |
 |Coding      | Long Arguments List Trend   | √ or x |  same as above                                             |
