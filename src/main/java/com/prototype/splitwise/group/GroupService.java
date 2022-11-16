package com.prototype.splitwise.group;

import com.prototype.splitwise.config.AuthContext;
import com.prototype.splitwise.entity.EntityRepository;
import com.prototype.splitwise.entity.EntityService;
import com.prototype.splitwise.entity.IDNameReference;
import com.prototype.splitwise.entity.PaginationRequest;
import com.prototype.splitwise.event.Event;
import com.prototype.splitwise.exception.ClientException;
import com.prototype.splitwise.user.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
final class GroupService extends EntityService<Group> {

    private final EntityService<User> userService;

    private final GroupRepository groupRepository;

    private GroupService(
            GroupRepository groupRepository,
            EntityService<User> userService,
            KafkaTemplate<String, Event<Group>> kafkaTemplate) {
        super(groupRepository, kafkaTemplate);
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    /**
     * Fetches all the groups the current authenticated user belongs to
     * @param paginationRequest Pagination request along with sort order on group's creation time
     * @return Paginated response of the user's groups
     */
    @Override
    public Page<Group> getPaginatedResponse(PaginationRequest paginationRequest) {
        return groupRepository.findByUserId(AuthContext.getCurrentUserOrElseThrow(), paginationRequest.getPageable());
    }

    /**
     * Validate the users in the group and also make the current user who created the group a member
     * @param resource Group to be created
     */
    @Override
    protected void beforeCreate(Group resource) {
        super.beforeCreate(resource);
        var groupUsers = resource.getData().getUsers();
        groupUsers.forEach(userRef -> {
                    var user = userService.getEntity(userRef.getId());
                    userRef.setName(user.getMeta().getName());
                });
        var currentUser = userService.getEntity(AuthContext.getCurrentUserOrElseThrow());
        var currentUserReference = IDNameReference.of(currentUser);
        groupUsers.add(currentUserReference);
    }

    /**
     * The user who does not belong to an existing group cannot modify it. <br>
     * Validate the users and also make the current user who's modifying the group a member if not present in the new resource
     * @param oldResource old group before modification
     * @param newResource modified group
     */
    @Override
    protected void beforeUpdate(Group oldResource, Group newResource) {
        super.beforeUpdate(oldResource, newResource);
        var currentUser = userService.getEntity(AuthContext.getCurrentUserOrElseThrow());
        var currentUserReference = IDNameReference.of(currentUser);
        var oldUsers = oldResource.getData().getUsers();
        if (!oldUsers.contains(currentUserReference)) {
            throw new ClientException(HttpStatus.FORBIDDEN, String.format("The user %s doesn't have access to update group %s", currentUser.getId(), oldResource.getId()));
        }
        var updatedUsers = newResource.getData().getUsers();
        updatedUsers.forEach(userRef -> {
            var user = userService.getEntity(userRef.getId());
            userRef.setName(user.getMeta().getName());
        });
        updatedUsers.add(currentUserReference);
    }

    @Override
    protected String getEntityType() {
        return Group.ENTITY_TYPE;
    }
}
