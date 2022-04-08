
资源文件/属性文件
<preload>=><import> 预先导入，预加载
res.app.* 应用配置/资源 res/app/root.xml;根配置
res.biz.* 业务配置/资源
res.cat.* 目录配置/资源
res.dev.*:开发配置/资源
res.env.*:环境配置/资源
res.fix.*:固定值

应用按模块子系统划分，业务按领域行业划分
res.app.<module>.* :res/app/spring/*.xml
res.biz.<domain>.* :res/app/recipe/*.xml

res.app.base.<module>-* ：框架基础配置，eg:res/app/base/spring-hold.xml
res.app.mybatis.* ：模块配置
res.app.ignite.* ：模块配置

res.biz.boot.<unit>-<func>
res.biz.art.
res.biz.p2p.
res.biz.o2o.



配置项key：第一节3字符，
app.* 应用配置项
biz.* 业务配置项

api.* 接口配置项
spi.* 服务配置项
web.* 前端配置项
css.* 样式管理
dbm.* 数据管理
run.* 运行时控制
res.* 资源文件配置
def.* 默认定义
oss.* 对象存储

txt.* i18n消息项





消息项key：
文本消息：txt.*
计量词汇：txt.unit.*：msg.unit.year,msg.unit.second,msg.unit.month...
编码消息：txt.code.[m3568],txt.code.[n3568] n负数(一般为错误e)，m正数(一般为消息)

组织名词：txt.team.<name>.
机构名词：txt.corp.gistlink=
角色名词：txt.role.admin=管理员
专有名词：txt.word.horse=马


