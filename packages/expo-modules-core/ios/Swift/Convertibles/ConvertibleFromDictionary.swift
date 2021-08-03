
/**
 A protocol that allows initializing the object with a dictionary.
 */
public protocol DictionaryConvertible: AnyMethodArgument {
  init()
  init(dictionary: [AnyHashable: Any?])
}

/**
 Provides the default implementation of `DictionaryConvertible` protocol.
 */
extension DictionaryConvertible {
  /**
   Initializes an object from given dictionary. Only members wrapped by `@bind` will be set in the object.
   */
  init(dictionary: [AnyHashable: Any?]) {
    self.init()

    forEachBoundMember(self) { key, binding in
      binding.set(dictionary[key] as Any)
    }
  }

  /**
   Converts an object back to the dictionary. Only members wrapped by `@bind` will be set in the dictionary.
   */
  public func toDictionary() -> [String: Any?] {
    var dict = [String: Any?]()

    forEachBoundMember(self) { key, binding in
      dict[key] = binding.get()
    }
    return dict
  }
}

protocol AnyBinding {
  var customKey: String? { get }

  func get() -> Any?
  func set(_ newValue: Any?)
}

fileprivate func forEachBoundMember(_ object: DictionaryConvertible, _ closure: (String, AnyBinding) -> Void) {
  Mirror(reflecting: object).children.forEach { (label, value) in
    guard let value = value as? AnyBinding,
          let key = value.customKey ?? normalizeMirrorChildLabel(label) else {
      return
    }
    closure(key, value)
  }
}

fileprivate func normalizeMirrorChildLabel(_ label: String?) -> String? {
  return (label != nil && label!.starts(with: "_")) ? String(label!.dropFirst()) : label
}

@propertyWrapper
public class bind<Type>: AnyBinding {
  public var wrappedValue: Type {
    return internalValue!
  }

  private(set) var internalValue: Type?
  private(set) var customKey: String?

  public init(key: String? = nil) {
    self.customKey = key
  }

  public init(wrappedValue: Type, key: String? = nil) {
    self.internalValue = wrappedValue
    self.customKey = key
  }

  func get() -> Any? {
    return wrappedValue
  }

  func set(_ newValue: Any?) {
    self.internalValue = newValue as? Type
  }
}

struct MyOptions: DictionaryConvertible {
  @bind var option: String?
}
