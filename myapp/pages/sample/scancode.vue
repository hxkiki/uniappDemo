<template>
	<view>
		<view class="container">
			<button type="primary" @click="scan1">{{ barcode1 }}</button>
		</view>
		<view class="container">
			<button type="primary" @click="scan2">{{ barcode2 }}</button>
		</view>
		<view class="container">
			<button type="primary" @click="scan3">{{ barcode3 }}</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			var index = 1;
			return {
				index: 1,
				barcode1:"请扫描条码",
				barcode2:"请扫描条码",
				barcode3:"请扫描条码",
			}
		},
		onLoad() {
			var me = this;
			var main = plus.android.runtimeMainActivity();
			var context = plus.android.importClass('android.content.Context');
			var receiver = plus.android.implements('io.dcloud.feature.internal.reflect.BroadcastReceiver', 
				{onReceive: doReceive});
				
			var IntentFilter = plus.android.importClass('android.content.IntentFilter');
			var Intent = plus.android.importClass('android.content.Intent');
			
			var filter = new IntentFilter();
			filter.addAction("nlscan.action.SCANNER_RESULT");
			main.registerReceiver(receiver, filter);
			
			function doReceive(content, intent) {
				plus.android.importClass("android.content.Intent");
				console.log(intent.getStringExtra("SCAN_BARCODE1"));
				if (me.$data.index == 1) {
					if (intent.getStringExtra("SCAN_BARCODE1") == null){
						me.barcode1 = "扫码失败,请重新扫描";
					}else {
						me.barcode1 = intent.getStringExtra("SCAN_BARCODE1");
					}
				}; 
				if (me.$data.index == 2) {
					if (intent.getStringExtra("SCAN_BARCODE1") == null){
						me.barcode2 = "扫码失败,请重新扫描";
					}else {
						me.barcode2 = intent.getStringExtra("SCAN_BARCODE1");
					}
				}; 
				if (me.$data.index == 3) {
					if (intent.getStringExtra("SCAN_BARCODE1") == null){
						me.barcode3 = "扫码失败,请重新扫描";
					}else {
						me.barcode3 = intent.getStringExtra("SCAN_BARCODE1");
					}
				}; 
			}
		},
		
		methods:{
			scan() {
				// var Intent = plus.android.importClass('android.content.Intent');
				// var intent = new Intent("nlscan.action.SCANNER_TRIG");
				// var main = plus.android.runtimeMainActivity();
				// main.sendBroadcast(intent)
				var suc = this;
				uni.scanCode({
					success:function(res){
						console.log(res.result);
						console.log(res.scanType);
						if (suc.$data.index == 1) 
							suc.barcode1 = res.result;
						if (suc.$data.index == 2)
							suc.barcode2 = res.result;
						if (suc.$data.index == 3)
							suc.barcode3 = res.result;
					}
				});
			},
			scan1() {
				this.$data.index = 1;
				this.scan();
			},
			scan2() {
				this.$data.index = 2;
				this.scan();
			},
			scan3() {
				this.$data.index = 3;
				this.scan();
			}
		}
		
	}
</script>

<style>
	.container {
		padding: 20px;
		font-size: 14px;
		line-height: 24px;
	}
</style>
