cmake_minimum_required(VERSION 3.22.1)

project("perkhub")

add_library(
        perkhub
        SHARED
        perkhub.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        perkhub
        ${log-lib}
)
