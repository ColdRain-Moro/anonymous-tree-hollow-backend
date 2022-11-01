package anonymous.tree.hollow.service

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.ObjectMetadata
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.region.Region
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

/**
 * anonymous.tree.hollow.service.CDNService.kt
 * AnonymousTreeHollow
 * 静态资源CDN相关交互 (主要是图床)
 * 默认使用腾讯云COS，也可以选择自行接入其他云服务商，修改本类代码即可
 *
 * @author 寒雨
 * @since 2022/11/1 上午1:10
 */
@Service
interface CDNService {

    /**
     * 没有contentLength的话有OOM的风险
     * contentLength直接从请求里拿就完事了
     *
     * @param fileName 为了防止新文件覆盖老文件，使用uuid或图片md5
     * @param input
     * @param contentLength
     */
    fun putObject(scope: String, fileName: String, input: InputStream, contentLength: Long? = null): String

    fun getObjectUrl(scope: String, fileName: String): String

}

class CDNServiceImpl : CDNService {

    @Value("\${anonymous-tree-hollow.cos.secret-id}")
    private lateinit var secretId: String
    @Value("\${anonymous-tree-hollow.cos.secret-key}")
    private lateinit var secretKey: String
    @Value("\${anonymous-tree-hollow.cos.cos-region}")
    private lateinit var cosRegion: String
    @Value("\${anonymous-tree-hollow.cos.bucket}")
    private lateinit var bucket: String

    private val client by lazy { initClient() }

    private fun initClient(): COSClient {
        val cred = BasicCOSCredentials(secretId, secretKey)
        val region = Region(cosRegion)
        val config = ClientConfig(region)
        config.httpProtocol = HttpProtocol.https
        return COSClient(cred, config)
    }

    override fun putObject(scope: String, fileName: String, input: InputStream, contentLength: Long?): String {
        val metadata = ObjectMetadata()
        contentLength?.let { metadata.contentLength = it }
        client.putObject(PutObjectRequest(bucket, "$scope/$fileName", input, metadata))
        return getObjectUrl(scope, fileName)
    }

    override fun getObjectUrl(scope: String, fileName: String): String {
        return client.getObjectUrl(bucket, "$scope/$fileName").toString()
    }
}