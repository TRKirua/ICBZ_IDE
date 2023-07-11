package fr.epita.assistants.backend.domain.service;

import fr.epita.assistants.backend.domain.entity.Node;
import fr.epita.assistants.backend.utils.Given;

@Given()
public interface NodeService {

    /**
     * Update the content in the range [from, to[.
     * The content must be inserted in any case.
     * i.e. : "hello world" -> update(0, 0, "inserted ") -> "inserted hello world"
     *      : "hello world" -> update(0, 5, "inserted ") -> "inserted world"
     *
     * @param node            Node to update (must be a file).
     * @param from            Beginning index of the text to update.
     * @param to              Last index of the text to update (Not included).
     * @param insertedContent Content to insert.
     * @return The node that has been updated.
     * @throws Exception upon update failure.
     */
    Node update(final Node node,
                final int from,
                final int to,
                final byte[] insertedContent);

    /**
     * Delete the node given as parameter.
     *
     * @param node Node to remove.
     * @return True if the node has been deleted, false otherwise.
     */
    boolean delete(final Node node);

    /**
     * Create a new node and the associated file/directory.
     *
     * @param folder Parent node of the new node.
     * @param name   Name of the new node.
     * @param type   Type of the new node.
     * @return Node that has been created.
     * @throws Exception upon creation failure.
     */
    Node create(final Node folder,
                final String name,
                final Node.Type type);

    /**
     * Move node from source to destination.
     *
     * @param nodeToMove        Node to move.
     * @param destinationFolder Destination of the node.
     * @return The node that has been moved.
     * @throws Exception upon move failure.
     */
    Node move(final Node nodeToMove,
              final Node destinationFolder);
}
