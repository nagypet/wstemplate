/*
 * Copyright 2020-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.perit.spvitamin.spring.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.perit.spvitamin.json.SpvitaminObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author Peter Nagy
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JSonSerializer
{
    public static String toJson(Object object) throws JsonProcessingException
    {
        return SpvitaminSpringObjectMapper.createMapper(SpvitaminObjectMapper.MapperType.JSON).writeValueAsString(object);
    }


    public static String toYaml(Object object) throws JsonProcessingException
    {
        return SpvitaminSpringObjectMapper.createMapper(SpvitaminObjectMapper.MapperType.YAML).writeValueAsString(object);
    }


    public static <T> T fromJson(String jsonString, Class<T> target) throws IOException
    {
        ObjectMapper mapper = SpvitaminSpringObjectMapper.createMapper(SpvitaminObjectMapper.MapperType.JSON);
        return mapper.readValue(jsonString, mapper.getTypeFactory().constructType(target));
    }


    public static <T> T fromYaml(String jsonString, Class<T> target) throws IOException
    {
        ObjectMapper mapper = SpvitaminSpringObjectMapper.createMapper(SpvitaminObjectMapper.MapperType.YAML);
        return mapper.readValue(jsonString, mapper.getTypeFactory().constructType(target));
    }
}
