/*
 * Copyright 2020-2023 the original author or authors.
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

package hu.perit.template.authservice.integration;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.exceptions.CsvException;
import hu.perit.spvitamin.core.exception.UnexpectedConditionException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Peter Nagy
 */

@Component
public class InputUserList implements Iterable<Map<String, String>>
{

    private static final String USERLIST_CSV = "Userlist.csv";
    private final List<Map<String, String>> csvContent = new ArrayList<>();

    public void loadFromFile() throws IOException, CsvException
    {
        try (InputStream userListStream = InputUserList.class.getClassLoader().getResourceAsStream(USERLIST_CSV))
        {
            if (userListStream == null || userListStream.available() == 0)
            {
                throw new UnexpectedConditionException(String.format("There is no ressource with name '%s'!", USERLIST_CSV));
            }

            RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder()
                    //.withQuoteChar(ICSVParser.NULL_CHARACTER)
                    .withSeparator(';')
                    .build();
            Reader reader = new InputStreamReader(userListStream);
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader)
                    .withCSVParser(rfc4180Parser);
            try (
                    CSVReader csvReader = csvReaderBuilder.build()
            )
            {
                List<String[]> records = csvReader.readAll();

                for (int i = 1; i < records.size(); i++)
                {
                    Map<String, String> row = new HashMap<>();
                    for (int j = 0; j < records.get(i).length; j++)
                    {
                        row.put(records.get(0)[j], records.get(i)[j].strip());
                    }
                    this.csvContent.add(row);
                }
            }
        }

    }

    @Override
    public Iterator<Map<String, String>> iterator()
    {
        return this.csvContent.iterator();
    }

    @Override
    public void forEach(Consumer<? super Map<String, String>> action)
    {
        this.csvContent.forEach(action);
    }

    @Override
    public Spliterator<Map<String, String>> spliterator()
    {
        return this.csvContent.spliterator();
    }
}
