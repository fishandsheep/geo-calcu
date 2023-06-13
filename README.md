# geo-calcu
根据经纬度计算距离的三种方式

| 单线程-100次   | mysql    | redis (14M) | memory   |
|------------|----------|-------------|----------|
| 内存占用       | 50M~160M | 50M~270M    | 84M~340M |
| max ms     | 863      | 132         | 606      |
| min ms     | 347      | 56          | 19       |
| avg ms     | 376      | 61          | 40       |
| fetch 500  | -        | 17s~27s     | -        |
| fetch 1000 | -        | 19s~32s     | -        |

