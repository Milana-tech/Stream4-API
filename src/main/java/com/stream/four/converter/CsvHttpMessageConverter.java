package com.stream.four.converter;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Component
public class CsvHttpMessageConverter<T> extends AbstractHttpMessageConverter<Object>
{

    private final CsvMapper csvMapper = new CsvMapper();

    public CsvHttpMessageConverter()
    {
        super(MediaType.parseMediaType("text/csv"));
    }

    @Override
    protected boolean supports(Class<?> clazz)
    {
        // Support Lists and individual objects
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException
    {
        throw new UnsupportedOperationException("CSV reading not supported");
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException
    {

        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody());

        if (object instanceof List)
        {
            List<?> list = (List<?>) object;
            if (!list.isEmpty())
            {
                Class<?> type = list.get(0).getClass();
                CsvSchema schema = csvMapper.schemaFor(type).withHeader();
                csvMapper.writer(schema).writeValue(writer, list);
            }
        }
        else
        {
            CsvSchema schema = csvMapper.schemaFor(object.getClass()).withHeader();
            csvMapper.writer(schema).writeValue(writer, object);
        }

        writer.flush();
    }
}