-- 导出
with region as (
	select * from (
		select row_number() over (partition by sub_region_id order by sub_region_id) rn, *
		from (
			select distinct sub_region_id, sub_region, region_id, region, city_id, city_cnname, B.provinceName, B.cityAreaCode
			from dbo.Dianping_City_SubRegion A left join dbo.Dianping_CityInfo B
			on A.city_id = B.cityId
		) temp
	) temp where rn = 1
), shopInfo as (
	select *
--	from (
--		select row_number() over (partition by shop_id order by shop_id asc) rn, *
	    from dbo.Dianping_ShopInfo_Budweiser_0608 
	    where primary_category_id = 'ch10' and category_id not in ('g113', 'g114', 'g1845', 'g116')
--	) temp where rn = 1
), detail as (
	select * from dbo.Dianping_Shop_Detail_Info A
	where EXISTS (select 1 from shopInfo where shopInfo.shop_id = A.shop_id)
)
--5221077
--select count(1)
--from (
--	(shopInfo A RIGHT join detail C on A.shop_id = C.shop_id) 
--	left join region B
--	on A.sub_region_id = B.sub_region_id)

select B.provinceName as '省份',
B.city_cnname as '城市',
B.region as '行政区',
B.sub_region as '热门商区',
A.shop_id as '店铺ID',
A.shop_name as '店铺名称',
A.shop_url as '店铺链接',
'美食' as '频道',
'中餐' as '分类',
A.self_category as '子分类',
A.address as '店铺地址',
B.cityAreaCode as '城市区号',
C.phone as '联系电话',
C.open_time as '营业时间',
A.avg_price as '人均消费',
A.star_level as '店铺星级',
C.review_num as '评论总数',
C.environment_score as '环境评分',
C.service_score as '服务评分',
C.taste_score as '口味评分',
C.longtitude as '经度',
C.latitude as '维度',
A.has_branch as '分店',
A.book_support as '预定',
A.tuan_support as '团购',
A.out_support as '外卖',
A.promotion_support as '促销'
from (
	(shopInfo A RIGHT join detail C on A.shop_id = C.shop_id) 
	left join region B
	on A.sub_region_id = B.sub_region_id)
order by city_cnname, region, sub_region;