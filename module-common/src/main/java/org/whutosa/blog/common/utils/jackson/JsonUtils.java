package org.whutosa.blog.common.utils.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author bobo
 * @date 2021/3/31
 */
@Slf4j
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern JSONP_PATTERN = Pattern.compile(".*?\\((\\{.*?})\\)");
    private static final Pattern SIGNLE_LINE_DEFINE_PATTERN = Pattern.compile("(?:(?:var)|(?:let)|(?:const))[ \\t]?.+?[ \\t]?=[ \\t]?(\\{.*});");

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        OBJECT_MAPPER.registerModule(timeModule);
    }

    /**
     * 按字段转换成JSON
     *
     * @apiNote 会忽略@JsonProperty注解
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", object, e);
        }
        return object.toString();
    }

    public static void printJson(Object object) {
        System.out.println(toJsonString(object));
    }

    public static JsonNode toJsonNode(Object object) {
        return parse(toJsonString(object));
    }

    public static JsonNode parse(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        }
        return OBJECT_MAPPER.createObjectNode();
    }

    /**
     * 解析并在出错的情况下抛出异常
     *
     * @param json json字符串
     * @return JsonNode
     * @throws IllegalArgumentException JSON格式错误
     */
    public static JsonNode parseWithExceptionThrowing(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        }
        throw new IllegalArgumentException("JSON格式错误");
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        }
        return new ArrayList<>();
    }

    public static <T> T parseObject(JsonNode json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Json解析错误 {}", json, e);
        }
        return null;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return parseObject(parse(json), clazz);
    }

    /**
     * 类似于FastJson的getJSONArray
     *
     * @param jsonNode JsonNode
     * @param key      子节点名
     * @return 子节点元素列表
     * @apiNote 只会获取一个子节点所含元素
     */
    public static List<JsonNode> getArray(JsonNode jsonNode, String key) {
        try {
            return parseList(OBJECT_MAPPER.writeValueAsString(jsonNode.get(key)), JsonNode.class);
        } catch (JsonProcessingException e) {
            log.error("获取Json数组出现异常 json:{} key:{} err:{}", jsonNode, key, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 类似于FastJson的getJSONArray
     *
     * @param source JsonNode
     * @param value  子节点名
     * @return 子节点元素列表
     * @apiNote 只会获取一个子节点所含元素
     */
    public static List<JsonNode> findList(JsonNode source, String value) {
        return source.findValues(value).stream()
                .flatMap(jsonNode -> StreamSupport.stream(jsonNode.spliterator(), false))
                .collect(Collectors.toList());
    }

    public static <T> List<T> findList(JsonNode source, String value, Class<T> clazz) {
        return source.findValues(value).stream()
                .flatMap(jsonNode -> StreamSupport.stream(jsonNode.spliterator(), false))
                .map(jsonNode -> OBJECT_MAPPER.convertValue(jsonNode, clazz))
                .collect(Collectors.toList());
    }

    /**
     * 类似于FastJson的parseArray
     *
     * @param json json
     * @return JSON数组所含元素组成的流
     */
    public static Stream<JsonNode> asStream(JsonNode json) {
        return StreamSupport.stream(json.spliterator(), false);
    }

    /**
     * 类似于FastJson的parseArray
     *
     * @param json json字符串
     * @return JSON数组所含元素组成的流
     */
    public static Stream<JsonNode> asStream(String json) {
        return StreamSupport.stream(parse(json).spliterator(), false);
    }

    /**
     * 类似于FastJson的parseArray
     *
     * @param json json字符串
     * @return JSON数组所含元素组成的列表
     */
    public static List<JsonNode> asList(String json) {
        return asStream(json).collect(Collectors.toList());
    }

    public static <T> Stream<T> asStream(String jsonString, Class<T> clazz) {
        return StreamSupport.stream(parse(jsonString).spliterator(), false).map(json -> parseObject(json, clazz));
    }

    public static <T> List<T> asList(String json, Class<T> clazz) {
        return asStream(json, clazz).collect(Collectors.toList());
    }

    /**
     * 将Object型结点转成键值对链表
     * <p>
     * Array型结点的元素没有名称，将返回空链表
     */
    public static LinkedList<AbstractMap.SimpleImmutableEntry<String, JsonNode>> asKeyValueList(JsonNode jsonNode) {
        LinkedList<AbstractMap.SimpleImmutableEntry<String, JsonNode>> result = new LinkedList<>();
        jsonNode.fieldNames().forEachRemaining(key -> result.add(new AbstractMap.SimpleImmutableEntry<>(key, jsonNode.get(key))));
        return result;
    }

    public static JsonNode blankJson() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    /**
     * @param jsonp jsonp回调js，形如dosomething({"example":123})
     * @return 回调参数中的json
     */
    public static JsonNode getJsonInJsonp(String jsonp) {
        return getJsonInRegexPatternGroup(jsonp, JSONP_PATTERN, 1);
    }

    /**
     * 在单行js变量或常量定义中获取json
     *
     * @param js 单行js代码
     * @return json
     * @apiNote 赋值式右值必须是字面常量，且行末有分号
     */
    public static JsonNode getJsonInSingleLineDefine(String js) {
        return getJsonInRegexPatternGroup(js, SIGNLE_LINE_DEFINE_PATTERN, 1);
    }

    public static JsonNode getJsonInRegexPatternGroup(String content, Pattern pattern, int groupIndex) {
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? parse(matcher.group(groupIndex)) : OBJECT_MAPPER.createObjectNode();
    }

    public static String getText(JsonNode jsonNode, String subNodeName) {
        return getTextOr(jsonNode, subNodeName, "");
    }

    public static <R> R getTextAndTransform(JsonNode jsonNode, String subNodeName, String defaultValue, Function<String, R> action) {
        return action.apply(getTextOr(jsonNode, subNodeName, defaultValue));
    }

    public static void getTextAndDoIfNotBlank(JsonNode jsonNode, String subNodeName, Consumer<String> action) {
        String text = getText(jsonNode, subNodeName);
        if (StringUtils.isNotBlank(text)) {
            action.accept(text);
        }
    }

    public static String getText(JsonNode jsonNode, int index) {
        return getTextOr(jsonNode, index, "");
    }

    public static String getTextOr(JsonNode jsonNode, int index, String defaultValue) {
        if (jsonNode.size() - 1 >= index) {
            return Optional.ofNullable(jsonNode.get(index))
                    .map(JsonNode::asText)
                    .orElse(defaultValue);
        }
        return defaultValue;
    }

    public static String getTextOr(JsonNode jsonNode, String subNodeName, String defaultValue) {
        return Optional.ofNullable(jsonNode.get(subNodeName))
                .map(JsonNode::asText)
                .orElse(defaultValue);
    }

    public static Double getDoubleOr(JsonNode jsonNode, String subNodeName, Double defaultValue) {
        return Optional.ofNullable(jsonNode.get(subNodeName))
                .map(JsonNode::asDouble)
                .orElse(defaultValue);
    }

    public static <R> R getDoubleAndTransform(JsonNode jsonNode, String subNodeName, Double defaultValue, Function<Double, R> action) {
        return action.apply(Optional.ofNullable(jsonNode.get(subNodeName))
                .map(JsonNode::asDouble)
                .orElse(defaultValue));
    }

    public static void getDoubleAndDoIfPresent(JsonNode jsonNode, String subNodeName, Consumer<Double> action) {
        Optional.ofNullable(jsonNode.get(subNodeName))
                .map(JsonNode::asDouble)
                .ifPresent(action);
    }

    public static Integer getIntegerOr(JsonNode jsonNode, String subNodeName, Integer defaultValue) {
        return Optional.ofNullable(jsonNode.get(subNodeName))
                .map(JsonNode::asInt)
                .orElse(defaultValue);
    }

    /**
     * 模糊搜索
     * <p>
     * 根据输入的条件在 node 中搜索匹配的 key, 默认返回遇到的第一个匹配的
     *
     * @param node       JsonNode
     * @param keyPattern 搜索的规则( xxx* / *xxx )
     * @return 搜索到的结果
     */
    public static JsonNode findNodeByKey(JsonNode node, String keyPattern) {
        if (StringUtils.isBlank(keyPattern)) {
            return blankJson();
        }
        if (!keyPattern.contains("*")) {
            return node.get(keyPattern);
        }
        String pattern = keyPattern.replace("*", "");
        int index = keyPattern.indexOf('*');
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            if (index != 0) {
                if (StringUtils.startsWith(entry.getKey(), pattern)) {
                    return entry.getValue();
                }
            } else {
                if (StringUtils.endsWith(entry.getKey(), pattern)) {
                    return entry.getValue();
                }
            }
        }
        return blankJson();
    }
}
