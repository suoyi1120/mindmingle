# MindMingle GPT 服务使用指南

本服务提供了与 OpenAI GPT 模型的集成，支持多种交互模式，包括普通文本问答和结构化 JSON 输出。

## 基本配置

在 `application.yml` 中已配置好 OpenAI API 相关设置：

```yaml
openai:
  api:
    # OpenAI API 密钥 - 实际使用时应通过环境变量注入
    key: ${OPENAI_API_KEY:your-api-key-here}
    # API 调用超时时间（秒）
    timeout: 60
    # 默认模型
    model: gpt-4o-mini
```

## API 接口说明

### 1. 基础文本问答

```
POST /api/gpt/generate?prompt=你的问题
```

直接返回模型生成的文本回答。

### 2. 对话式问答

```
POST /api/gpt/chat
Content-Type: application/json

{
  "model": "gpt-4o-mini",
  "messages": [
    {"role": "user", "content": "你好，请介绍一下自己"}
  ],
  "temperature": 0.7,
  "max_tokens": 800
}
```

返回完整的 ChatGPT 响应对象。

### 3. 结构化输出 (JSON Schema)

```
POST /api/gpt/structured?prompt=为我生成一个每周健身计划
Content-Type: application/json

{
  "type": "object",
  "properties": {
    "weeklyPlan": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "day": {"type": "string"},
          "exercises": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "name": {"type": "string"},
                "sets": {"type": "integer"},
                "reps": {"type": "integer"},
                "duration": {"type": "string"}
              }
            }
          },
          "restDay": {"type": "boolean"}
        }
      }
    },
    "caloriesBurned": {"type": "integer"},
    "difficulty": {"type": "string", "enum": ["beginner", "intermediate", "advanced"]}
  }
}
```

返回符合指定 JSON Schema 的结构化输出。

### 4. 高级结构化对话

```
POST /api/gpt/structured-chat?jsonSchema={"type":"object","properties":{"answer":{"type":"string"},"confidence":{"type":"number"}}}
Content-Type: application/json

{
  "model": "gpt-4o-mini",
  "messages": [
    {"role": "user", "content": "2+2等于多少?"}
  ],
  "temperature": 0.7,
  "max_tokens": 800
}
```

返回符合指定 JSON Schema 的结构化 ChatGPT 响应对象。

## 使用示例

下面是一个使用结构化输出生成健康计划的示例：

```java
// 使用 JSON Schema 定义输出结构
String jsonSchema = """
{
  "type": "object",
  "properties": {
    "mealPlan": {
      "type": "object",
      "properties": {
        "dailyMeals": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "day": { "type": "string" },
              "mealDescription": { "type": "string" }
            }
          }
        },
        "dailyNutrition": {
          "type": "object",
          "properties": {
            "totalCalories": { "type": "string" },
            "protein": { "type": "string" },
            "carbohydrates": { "type": "string" },
            "fat": { "type": "string" }
          }
        }
      }
    },
    "fitnessPlan": {
      "type": "object",
      "properties": {
        "dailyWorkouts": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "day": { "type": "string" },
              "exercise": { "type": "string" },
              "description": { "type": "string" }
            }
          }
        }
      }
    }
  }
}
""";

// 调用 GPT 服务
String prompt = "为一名30岁的办公室工作者设计一套健康的饮食和锻炼计划，目标是减轻5公斤体重";
String response = gptService.generateStructuredResponse("gpt-4o-mini", prompt, jsonSchema);

// 解析 JSON 响应
ObjectMapper mapper = new ObjectMapper();
JsonNode planNode = mapper.readTree(response);

// 使用结构化数据
JsonNode mealPlan = planNode.get("mealPlan");
JsonNode dailyMeals = mealPlan.get("dailyMeals");
for (JsonNode meal : dailyMeals) {
    System.out.println("日期: " + meal.get("day").asText());
    System.out.println("餐食: " + meal.get("mealDescription").asText());
}
```

## 注意事项

1. 对于复杂的 JSON Schema，请确保模型有足够的 tokens 生成完整的响应
2. 实际生产环境中应通过环境变量注入 API 密钥，避免硬编码
3. 使用 System Message 可以进一步指导模型生成符合要求的输出 