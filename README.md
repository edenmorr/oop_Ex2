# EX 2

### Part 1:
in part one of the assignment we were asked to make a line file reader and compare
several ways or reading number of lines:
1. Single main thread
2. Thread array
3. Thread pool

each way of reading has its own unique characteristics
1. Runs reads lines in a synchronous way file after file
2. Creates a thread ,that read lines, for each file and adds them to an array
2. Creates a thread ,that read lines, for each file and adds them to a pool

in theory if we order the execution time from fastest to slowest it should be:
thread pool -> thread array -> single thread

time comparison for reading fallowing the pdf instructions: (seen in test)
file count : 100
max line count : 1000

|    Method     |   Avg Time    |
| ------------- |--------------:|
| Single thread | ~29ms         |
| Thread array  | ~6ms          |
| Thread pool   | ~8ms          | 

these results dont align with what we wouldve expected, this is mainly because of the
pool size, after changing the pool size to the number of cores we get: 

|    Method     |   Avg Time    |
| ------------- |--------------:|
| Single thread | ~20ms         |
| Thread array  | ~8ms          |
| Thread pool   | ~4ms          | 

(more indepth explenations inside the test part1TestReadTimes)

we use 3 helper classes to make the code cleaner:
1. LineCounterCallable
2. LineCounterThread
3. LineCoutner
such that 1 & 2 use class 3 to read files from line 

in addition 2 helper methonds for file creation and line writing
createFile(..)
writeLinesToFile(..)

and 1 helper function for storage managing:
deleteFiles(..) - deletes all txt files we created
