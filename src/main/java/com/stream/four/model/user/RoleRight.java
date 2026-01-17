package com.stream.four.model.user;

import com.stream.four.model.enums.Role;
import jakarta.persistence .*;
import lombok .*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table (name = "RoleRight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoleRight
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "RoleID")
    private Long roleId;

    @Enumerated (EnumType.STRING)
    @Column (name = "RoleName", nullable = false, unique = true)
    private Role roleName;

    @Column (name = "Description")
    private String description;

    @Column (name = "Permissions", columnDefinition = "TEXT")
    private String permissions;  // JSON or comma-separated list

    @CreationTimestamp
    @Column (name = "CreatedAt")
    private LocalDateTime createdAt;
}