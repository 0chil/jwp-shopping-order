package cart.dto;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.restdocs.payload.FieldDescriptor;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import com.epages.restdocs.apispec.Schema;

public enum ResponseSnippets {
    PRODUCT(ProductResponse.class, () -> Map.of(
            "id", "제품 id",
            "name", "제품 명",
            "price", "제품 가격",
            "imageUrl", "제품 이미지 url"
    )),
    CART_ITEM(CartItemResponse.class, () -> join(
            Map.of(
                    "id", "카트 id",
                    "quantity", "수량",
                    "product", "제품 정보"
            ),
            withPrefix("product.", PRODUCT.fieldsSupplier.get())
    ));

    private static final String EMPTY = "";
    private static final String LIST_PREFIX = "[].";

    private final Class<?> clazz;
    private final Supplier<Map<String, String>> fieldsSupplier;

    ResponseSnippets(Class<?> clazz, Supplier<Map<String, String>> fieldsSupplier) {
        this.clazz = clazz;
        this.fieldsSupplier = fieldsSupplier;
    }

    public static Optional<ResourceSnippetParametersBuilder> resourceBuilderOf(Object dto) {
        return Arrays.stream(values())
                .filter(that -> that.clazz.equals(getClassOf(dto)))
                .map(it -> it.toResourceBuilderOf(dto))
                .findFirst();
    }

    private ResourceSnippetParametersBuilder toResourceBuilderOf(Object dto) {
        return ResourceSnippetParameters.builder()
                .responseSchema(new Schema(getNameOf(dto)))
                .responseFields(toFields(withPrefix(getPrefixOf(dto), fieldsSupplier.get())));
    }

    private String getNameOf(Object dto) {
        String name = this.name();
        if (isList(dto)) {
            name += "S";
        }
        return name;
    }

    private static List<FieldDescriptor> toFields(Map<String, String> fields) {
        return fields.entrySet().stream()
                .map(entry -> fieldWithPath(entry.getKey()).description(entry.getValue()))
                .collect(Collectors.toList());
    }

    private static Map<String, String> withPrefix(String prefix, Map<String, String> fields) {
        return fields.entrySet().stream()
                .map(entry -> Map.entry(prefix + entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map<String, String> join(Map<String, String> fields, Map<String, String> otherFields) {
        HashMap<String, String> newFields = new HashMap<>(fields);
        newFields.putAll(otherFields);
        return newFields;
    }

    private static <T> Class<?> getClassOf(T dto) {
        if (dto instanceof List) {
            return ((List<?>)dto).stream()
                    .findFirst()
                    .map(Object::getClass)
                    .orElseThrow(() -> new IllegalArgumentException("빈 리스트입니다"));
        }
        return dto.getClass();
    }

    private static <T> String getPrefixOf(T dto) {
        if (isList(dto)) {
            return LIST_PREFIX;
        }
        return EMPTY;
    }

    private static <T> boolean isList(T dto) {
        return dto instanceof List;
    }
}
