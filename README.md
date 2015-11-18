# Android-PullToRefreshRecyclerView

![Screenshot](https://github.com/HomHomLin/Android-PullToRefreshRecyclerView/blob/master/screenshot.gif)

这是一个可以下拉刷新的RecyclerView，并且支持方便添加Header、滑动到底部自动加载更多以及其他ListView的功能。

当前版本：v1.0.0

## 特性
 * 基于原生RecyclerView的封装
 * 支持下拉刷新
 * 支持滑动到底部自动加载更多
 * 实现了ListView大部分API
 * 支持方便添加Header头部（原生RecyclerView不支持）
 * 支持设置EmptyView
 * 目前支持的LayoutManager模式:
 	* **LinearLayoutManager**
 	* **GridLayoutManager**

项目位置： <https://github.com/HomHomLin/Android-PullToRefreshRecyclerView>.

## Sample
项目内含有Sample程序:[Sample](https://github.com/HomHomLin/Android-PullToRefreshRecyclerView/blob/master/sample.apk)

##Using library in your application
If you are building with Gradle, simply add the following line to the dependencies section of your build.gradle file:

dependencies {
     compile 'homhomlin.lib:ptrrv-library:1.0.0'
}

##License
Copyright 2015 LinHongHong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.