with region as (
    select distinct sub_region_id, sub_region, region_id, region, city_id, city_cnname, B.provinceName, B.cityAreaCode
    from dbo.Dianping_City_SubRegion A left join dbo.Dianping_CityInfo B
    on A.city_id = B.cityId
), shopInfo as (
    select row_number() over (partition by shop_id order by shop_id asc) rn, *
    from dbo.Dianping_ShopInfo_Budweiser
    where category_id = 'g116'
)
select top 10 B.provinceName as '省份',
B.city_cnname as '城市',
B.region as '行政区',
B.sub_region as '热门商区',
A.shop_id as '店铺ID',
A.shop_name as '店铺名称',
A.shop_url as '店铺链接',
'美食' as '频道',
'西餐' as '分类',
A.address as '店铺地址',
B.cityAreaCode as '城市区号',
'' as '联系电话',
'' as '营业时间',
A.avg_price as '人均消费',
A.star_level as '店铺星级',
A.review_num as '评论总数',
A.environment_score as '环境评分',
A.service_score as '服务评分',
A.taste_score as '口味评分',
'' as '经度',
'' as '维度',
A.has_branch as '分店',
A.book_support as '预定',
A.tuan_support as '团购',
A.out_support as '外卖',
A.promotion_support as '促销'
from shopInfo A left join region B
on A.city_id = B.city_id;