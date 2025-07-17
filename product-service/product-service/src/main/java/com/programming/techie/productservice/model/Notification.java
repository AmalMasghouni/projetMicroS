package com.programming.techie.productservice.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification implements Serializable {

    private String id;
    private String userId;
    private String externalId;
    private String title;
    private String shortDescription;
    private String description;
    private String status;
    private String checked;
    private String notificationType;
    private LocalDate date;

}