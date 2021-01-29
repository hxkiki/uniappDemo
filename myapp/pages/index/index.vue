<template>
	<view class="container">
		<view>
			<view class="mytext">uniapp框架学习测试</view>
		</view>
		<view class="uni-panel" v-for="(item, index) in list" :key="item.id">
			<view class="uni-panel-h" :class="item.open ? 'uni-panel-h-on' : ''" @click="triggerCollapse(index)">
				<text class="uni-panel-text">{{item.name}}</text>
			</view>
		</view>
	</view>
</template>
<script>
	export default {
		data() {
			return {
				list: [{
					id: 'scancode',
					name: '扫码演示',
					open: true, 
					url: '/pages/sample/scancode'
				},
				{
					id: 'bluetooth',
					name: '蓝牙打印测试(完成)',
					open: true,
					url: '/pages/sample/bluetooth'
				},
				{
					id:'message',
					name: '消息推送测试(未完成)',
					open: true,
					url:'/pages/sample/message'
				}],
				navigateFlag: false
			}
		},
		onLoad() {},
		methods: {
			triggerCollapse(e) {
				if (!this.list[e].pages) {
					this.goDetailPage(this.list[e].url);
					return;
				}
				for (var i = 0; i < this.list.length; ++i) {
					if (e === i) {
						this.list[i].open = !this.list[e].open;
					} else {
						this.list[i].open = false;
					}
				}
			},
			goDetailPage(e) {
				if (this.navigateFlag) {
					return;
				}
				this.navigateFlag = true;
				uni.navigateTo({
					url: e
				});
				setTimeout(() => {
					this.navigateFlag = false;
				}, 200)
				return false;
			}
		}
	}
</script>

<style>
	.container {
		padding: 20px;
		font-size: 14px;
		line-height: 24px;
		align-items: center;
	},
	.mytext{
		color: #7A7E83;
		font-size: 14px;
		line-height: 20px;
		width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
	}
</style>
