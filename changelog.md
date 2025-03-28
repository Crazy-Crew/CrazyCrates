- Added the ability to use `Item Models` which was introduced in 1.21.4
  - This acts as replacement to `Custom-Model-Data` wherever you see it, It's up to the plugin/resource pack you use to find the new id.
    - oraxen:emerald_helmet
    - nexo:emerald_helmet
    - itemsadder:emerald_helmet
  - Above are examples of namespace:id.
```yml
  PhysicalKey:
    # Name of the Key.
    Name: "<bold><gradient:#084CFB:#ADF3FD>Wonder Crate Key</gradient></bold>"
    # Lore of the Key.
    Lore:
      - "<gradient:#084CFB:#ADF3FD>A fancy key to open a wonderful crate!</gradient>"
    # The item the key is.
    Item: "blue_dye"
    # The item model, Mojang introduced this in 1.21.4... this replaces custom model data!
    # Set this to blank for it to do nothing.
    # The format is namespace:id
    Model:
      # The namespace i.e. nexo
      Namespace: ""
      # The id i.e. emerald_helmet
      Id: ""
    # Makes the key look enchanted.
    Glowing: true 
```
- Custom-Model-Data, or custom-model-data can accept numbers or strings due to recent changes in 1.21.4.