# CPD

## Instructions

### Instructions for c++

- You should go to the directory of papi and run this command:

```
pedrobala@pedrobalapc:~/Desktop/papi-7.0.0$ sudo sh -c 'echo -1 >/proc/sys/kernel/perf_event_paranoid'
```

- Then, inside the directory of your project you should run this commands to compile and execute your c++ file and test the different algorithms:

```
pedrobala@pedrobalapc:~/CPD$ g++ -O2 matrixproduct.cpp -o fileout -lpapi
pedrobala@pedrobalapc:~/CPD$ ./fileout
```

### Instructions for julia

- Inside the directory of you project, you should run this commands in the terminal to compile and execute your julia file and then you can test the different algorithms:

```
pedrobala@pedrobalapc:~/CPD$ julia
               _
   _       _ _(_)_     |  Documentation: https://docs.julialang.org
  (_)     | (_) (_)    |
   _ _   _| |_  __ _   |  Type "?" for help, "]?" for Pkg help.
  | | | | | | |/ _` |  |
  | | |_| | | | (_| |  |  Version 1.8.1 (2022-09-06)
 _/ |\__'_|_|_|\__'_|  |  Official https://julialang.org/ release
|__/                   |
julia> include("matrixproduct.jl")
```