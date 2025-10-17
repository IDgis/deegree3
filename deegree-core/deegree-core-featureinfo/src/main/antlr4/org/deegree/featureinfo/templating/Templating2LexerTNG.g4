lexer grammar Templating2LexerTNG;

tokens {
    ID,
    Name,
    TagClose,
    Colon
}

TemplateDefinitionStart: '<?template' -> pushMode(DEFINE_TAG);
MapDefinitionStart: '<?map' -> pushMode(DEFINE_TAG);

FeatureCallStart: '<?feature' -> pushMode(CALL_TAG);
PropertyCallStart: '<?property' -> pushMode(CALL_TAG);

NameStart: '<?name' -> pushMode(SIMPLE_CALL_TAG);
ValueStart: '<?value' -> pushMode(SIMPLE_CALL_TAG);
OddStart: '<?odd' -> pushMode(SIMPLE_CALL_TAG);
EvenStart: '<?even' -> pushMode(SIMPLE_CALL_TAG);

LinkStart: '<?link' -> pushMode(LINK_TAG);

Index: '<?index>';
GmlId: '<?gmlid>';
ExplicitEnd: '</?>';
Text: ('<' ~'?' | ~'<')+;

fragment WS_BODY: [ \t\r\n]+;
fragment ID_BODY: [a-zA-Z0-9_]+;
fragment NAME_BODY: ('a'..'z' | 'A'..'Z' | '0'..'9' | '_' | '-' | '"' | '\u0080'..'\ufffe')+;

mode DEFINE_TAG;
DefinitionID: ID_BODY -> type(ID);
DefinitionWS: WS_BODY -> skip;
DefinitionTagClose: '>' -> type(TagClose), popMode;

mode CALL_TAG;
Not: 'not';
BracketLeft: '(';
BracketRight: ')';
Star: '*';
Comma: ',';
CallName: NAME_BODY -> type(Name);
CallColon: ':' -> type(Colon), pushMode(CALL_TAG_ID);
CallWS: WS_BODY -> skip;
CallTagClose: '>' -> type(TagClose), popMode;

mode CALL_TAG_ID;
CallID: ID_BODY -> type(ID), popMode;

mode SIMPLE_CALL_TAG;
SimpleCallColon: ':' -> type(Colon);
SimpleCallID: ID_BODY -> type(ID);
SimpleCallTagClose: '>' -> type(TagClose), popMode;

mode LINK_TAG;
LinkBody: ~'>'+;
LinkEnd: '>' -> type(TagClose), popMode;
