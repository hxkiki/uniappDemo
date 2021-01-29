<template>
	<view class="container">
		<view class="uni-panel" v-for="item in dataList" :key="item.id">
			<view class="uni-panel-h-on" @click="connect(item.address)">
				<text class="uni-panel-text">型号: {{item.name}}\n</text>
				<text class="uni-panel-text">地址: {{item.address}}</text>
			</view>
		</view>
	</view>
	
</template>

<script>
	var printUtil = uni.requireNativePlugin('ScanModule');
	var modal = uni.requireNativePlugin(modal);
	
	export default {
		data() {
			return {
				dataList: [{"id": 1, "name": "hello kitty", "address": "01:01:01:01:01:01"}],
			}
		},
		onLoad() {
			console.log('onload');
			this.searchDevice();
		},
		methods: {
			searchDevice() {
				console.log('search');
				printUtil.print(res => {
						// var str = JSON.stringify(res);
						this.dataList = res.res;
					}	
				)
			},
			
			connect(address) {
				console.log(address);
				printUtil.connect({code : address}, res => {
						// var str = JSON.stringify(res);
						console.log(res.code);
					}	
				)
			}
		}
	}
</script>

<style>

</style>
