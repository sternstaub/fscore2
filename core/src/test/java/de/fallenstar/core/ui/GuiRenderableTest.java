package de.fallenstar.core.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für das GuiRenderable Interface.
 *
 * <p>Testet das Self-Rendering Pattern mit Mock-Implementierungen.</p>
 *
 * <p><b>Hinweis:</b> Diese Tests verwenden gemockte ItemStacks, da echte
 * ItemStack-Erstellung einen gemockten Bukkit-Server benötigt. Vollständige
 * ItemStack-Tests sollten in Integration-Tests durchgeführt werden.</p>
 */
class GuiRenderableTest {

    /**
     * Einfache Test-Implementierung die immer sichtbar ist.
     */
    private static class SimpleRenderable implements GuiRenderable {
        private final ItemStack mockItem;

        public SimpleRenderable(ItemStack mockItem) {
            this.mockItem = mockItem;
        }

        @Override
        public ItemStack getDisplayItem() {
            return mockItem;
        }
    }

    /**
     * Test-Implementierung mit Permission-Check.
     */
    private static class PermissionRenderable implements GuiRenderable {
        private final String permission;
        private final ItemStack mockItem;

        public PermissionRenderable(String permission, ItemStack mockItem) {
            this.permission = permission;
            this.mockItem = mockItem;
        }

        @Override
        public ItemStack getDisplayItem() {
            return mockItem;
        }

        @Override
        public boolean isVisible(Player player) {
            return player.hasPermission(permission);
        }
    }

    /**
     * Test-Implementierung die niemals sichtbar ist.
     */
    private static class InvisibleRenderable implements GuiRenderable {
        private final ItemStack mockItem;

        public InvisibleRenderable(ItemStack mockItem) {
            this.mockItem = mockItem;
        }

        @Override
        public ItemStack getDisplayItem() {
            return mockItem;
        }

        @Override
        public boolean isVisible(Player player) {
            return false;
        }
    }

    private Player player;
    private ItemStack mockItem;

    @BeforeEach
    void setUp() {
        player = mock(Player.class);
        mockItem = mock(ItemStack.class);
    }

    @Test
    void testGetDisplayItem_ReturnsItemStack() {
        // Arrange
        GuiRenderable renderable = new SimpleRenderable(mockItem);

        // Act
        ItemStack item = renderable.getDisplayItem();

        // Assert
        assertNotNull(item, "getDisplayItem() sollte niemals null zurückgeben");
        assertSame(mockItem, item, "Sollte das konfigurierte ItemStack zurückgeben");
    }

    @Test
    void testGetDisplayItem_CalledMultipleTimes() {
        // Arrange
        GuiRenderable renderable = new SimpleRenderable(mockItem);

        // Act
        ItemStack item1 = renderable.getDisplayItem();
        ItemStack item2 = renderable.getDisplayItem();
        ItemStack item3 = renderable.getDisplayItem();

        // Assert
        assertSame(mockItem, item1, "Erster Aufruf sollte Mock zurückgeben");
        assertSame(mockItem, item2, "Zweiter Aufruf sollte Mock zurückgeben");
        assertSame(mockItem, item3, "Dritter Aufruf sollte Mock zurückgeben");
    }

    @Test
    void testGetDisplayItem_NeverReturnsNull() {
        // Arrange
        GuiRenderable renderable = new GuiRenderable() {
            @Override
            public ItemStack getDisplayItem() {
                return mockItem;
            }
        };

        // Act
        ItemStack item = renderable.getDisplayItem();

        // Assert
        assertNotNull(item, "getDisplayItem() sollte niemals null zurückgeben");
    }

    @Test
    void testIsVisible_DefaultImplementationReturnsTrue() {
        // Arrange
        GuiRenderable renderable = new SimpleRenderable(mockItem);

        // Act
        boolean visible = renderable.isVisible(player);

        // Assert
        assertTrue(visible, "Default-Implementierung sollte immer true zurückgeben");
    }

    @Test
    void testIsVisible_WithPermission_ReturnsTrue() {
        // Arrange
        when(player.hasPermission("test.permission")).thenReturn(true);
        GuiRenderable renderable = new PermissionRenderable("test.permission", mockItem);

        // Act
        boolean visible = renderable.isVisible(player);

        // Assert
        assertTrue(visible, "Sollte true zurückgeben wenn Player Permission hat");
        verify(player).hasPermission("test.permission");
    }

    @Test
    void testIsVisible_WithoutPermission_ReturnsFalse() {
        // Arrange
        when(player.hasPermission("test.permission")).thenReturn(false);
        GuiRenderable renderable = new PermissionRenderable("test.permission", mockItem);

        // Act
        boolean visible = renderable.isVisible(player);

        // Assert
        assertFalse(visible, "Sollte false zurückgeben wenn Player keine Permission hat");
        verify(player).hasPermission("test.permission");
    }

    @Test
    void testIsVisible_CustomImplementation_CanReturnFalse() {
        // Arrange
        GuiRenderable renderable = new InvisibleRenderable(mockItem);

        // Act
        boolean visible = renderable.isVisible(player);

        // Assert
        assertFalse(visible, "Custom-Implementierung kann false zurückgeben");
    }

    @Test
    void testMultipleRenderables_WithDifferentVisibility() {
        // Arrange
        when(player.hasPermission("perm.admin")).thenReturn(true);
        when(player.hasPermission("perm.user")).thenReturn(false);

        ItemStack mockItem1 = mock(ItemStack.class);
        ItemStack mockItem2 = mock(ItemStack.class);
        ItemStack mockItem3 = mock(ItemStack.class);
        ItemStack mockItem4 = mock(ItemStack.class);

        GuiRenderable alwaysVisible = new SimpleRenderable(mockItem1);
        GuiRenderable adminOnly = new PermissionRenderable("perm.admin", mockItem2);
        GuiRenderable userOnly = new PermissionRenderable("perm.user", mockItem3);
        GuiRenderable neverVisible = new InvisibleRenderable(mockItem4);

        // Act & Assert
        assertTrue(alwaysVisible.isVisible(player), "Always visible sollte sichtbar sein");
        assertTrue(adminOnly.isVisible(player), "Admin-only sollte für Admin sichtbar sein");
        assertFalse(userOnly.isVisible(player), "User-only sollte für Admin nicht sichtbar sein");
        assertFalse(neverVisible.isVisible(player), "Never visible sollte nicht sichtbar sein");
    }

    @Test
    void testIsVisible_WithNullPlayer_HandledByImplementation() {
        // Arrange
        GuiRenderable renderable = new GuiRenderable() {
            @Override
            public ItemStack getDisplayItem() {
                return mockItem;
            }

            @Override
            public boolean isVisible(Player player) {
                return player != null; // Implementierung entscheidet über null-Handling
            }
        };

        // Act & Assert
        assertFalse(renderable.isVisible(null), "Sollte false bei null Player zurückgeben");
        assertTrue(renderable.isVisible(player), "Sollte true bei nicht-null Player zurückgeben");
    }

    @Test
    void testDefaultImplementation_AlwaysVisible() {
        // Arrange
        GuiRenderable renderable = new GuiRenderable() {
            @Override
            public ItemStack getDisplayItem() {
                return mockItem;
            }
            // Verwendet default isVisible() Implementierung
        };

        // Act
        boolean visibleForPlayer1 = renderable.isVisible(player);
        boolean visibleForPlayer2 = renderable.isVisible(mock(Player.class));

        // Assert
        assertTrue(visibleForPlayer1, "Default sollte für alle Player true sein");
        assertTrue(visibleForPlayer2, "Default sollte für alle Player true sein");
    }

    @Test
    void testInterfaceContract_GetDisplayItemMustNotReturnNull() {
        // Arrange
        GuiRenderable renderable = new SimpleRenderable(mockItem);

        // Act
        ItemStack item = renderable.getDisplayItem();

        // Assert
        assertNotNull(item, "Interface-Vertrag: getDisplayItem() darf niemals null zurückgeben");
    }

    @Test
    void testInterfaceContract_IsVisibleMustAcceptNullGracefully() {
        // Arrange - Implementierung die null prüft
        GuiRenderable safeRenderable = new GuiRenderable() {
            @Override
            public ItemStack getDisplayItem() {
                return mockItem;
            }

            @Override
            public boolean isVisible(Player player) {
                if (player == null) {
                    return false;
                }
                return true;
            }
        };

        // Act & Assert
        assertDoesNotThrow(() -> safeRenderable.isVisible(null),
            "isVisible() sollte null gracefully behandeln");
    }

    /*
     * HINWEIS: Tests für ItemStack-Details (Material, DisplayName, Lore)
     * benötigen einen gemockten Bukkit-Server (Bukkit.getItemFactory()).
     *
     * Diese Tests sollten als Integration-Tests mit MockBukkit
     * durchgeführt werden.
     *
     * Beispiele für zu testende Funktionalität:
     * - getDisplayItem() liefert korrektes Material
     * - getDisplayItem() hat DisplayName gesetzt
     * - getDisplayItem() hat Lore konfiguriert
     * - getDisplayItem() hat Enchantments
     */
}
